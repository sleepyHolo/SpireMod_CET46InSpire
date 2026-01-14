package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import com.badlogic.gdx.math.MathUtils;
import com.stemlaur.anki.domain.catalog.CardDetail;
import com.stemlaur.anki.domain.catalog.DeckService;
import com.stemlaur.anki.domain.catalog.spi.fake.InMemoryDecks;
import com.stemlaur.anki.domain.catalog.spi.fake.SimpleDeckIdFactory;
import com.stemlaur.anki.domain.common.spi.fake.FakeDomainEvents;
import com.stemlaur.anki.domain.study.*;
import com.stemlaur.anki.domain.study.spi.fake.InMemoryCardProgresses;
import com.stemlaur.anki.domain.study.spi.fake.InMemorySessions;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.util.*;
import java.util.stream.IntStream;

/**
 * 构造QuizData所需的变量
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildQuizDataRequest {
    LexiconEnum usingLexicon;
    int vocabularySize;
    int targetId;
    String targetUiStringsId;
    String uiStringsIdStart;
    protected int maxOptionNum;

    /**
     * 一种取题策略。
     */
    public interface IFactory {
        /**
         * 取一个随机题
         */
        BuildQuizDataRequest fromRandom(LexiconEnum lexicon);

        /**
         * 接收答题结果。可能会因此调整后续取出的题。
         */
        void afterQuiz(boolean isPerfect);

        /**
         * 接收游戏receivePostInitialize通知
         */
        void initMap(Map<LexiconEnum, Integer> needLoadBooks);

    }

    /**
     * FSRS（Free Spaced Repetition Scheduler）
     * 自由间隔重复调度算法
     * <a href="https://github.com/stemlaur/anki">...</a>
     */
    public static class FSRSFactory implements IFactory {
        private static final Logger logger = LogManager.getLogger(FSRSFactory.class.getName());
        public static FSRSFactory INSTANCE = new FSRSFactory();

        private final DeckService deckService;
        private final DeckStudyService deckStudyService;
        private final Map<LexiconEnum, String> deskIdMap = new HashMap<>();
        private final Map<LexiconEnum, String> studySessionIdMap = new HashMap<>();
        private LexiconEnum currentLexicon;
        private String currentCardId;
        FSRSFactory() {
            this.deckService = new DeckService(new InMemoryDecks(), new SimpleDeckIdFactory(), new FakeDomainEvents());
            this.deckStudyService = new DeckStudyService(deckService, new CardProgressService(new InMemoryCardProgresses()), new SessionIdFactory(), new InMemorySessions(), Clock.systemUTC());
        }

        @Override
        public BuildQuizDataRequest fromRandom(LexiconEnum lexicon) {
            final Optional<CardToStudy> optionalCardToStudy = this.deckStudyService.nextCardToStudy(studySessionIdMap.get(lexicon));
            CardToStudy card = optionalCardToStudy.orElseThrow(() -> new NoSuchElementException("No value present of optionalCardToStudy"));
            this.currentCardId = card.id();
            this.currentLexicon = lexicon;
            logger.info("currentCardId set to {}", currentCardId);
            // 特殊地使用CardDetail：存储LexiconEnum和卡片index
            LexiconEnum lexiconEnum = LexiconEnum.valueOf(card.question());
            int index = Integer.parseInt(card.answer());
            return FactoryUtils.fromTargetIndex(lexiconEnum, index);
        }

        @Override
        public void afterQuiz(boolean isPerfect) {
            logger.info("afterQuiz by currentCardId = {}, currentLexicon = {}", currentCardId, currentLexicon);
            this.deckStudyService.study(studySessionIdMap.get(currentLexicon), this.currentCardId, isPerfect ? Opinion.GREEN : Opinion.RED);
        }

        @Override
        public void initMap(Map<LexiconEnum, Integer> needLoadBooks) {
            Map<LexiconEnum, List<BuildQuizDataRequest>> requstMap = new HashMap<>();
            needLoadBooks.forEach((k, v) -> {
                int size = BookConfig.VOCABULARY_MAP.get(k);
                String deskId = this.deckService.create("USELESS TITLE");
                deskIdMap.put(k, deskId);
                IntStream.range(0, size)
                        .forEach(i -> {
                            // 特殊地使用CardDetail：存储LexiconEnum和卡片index
                            this.deckService.addCard(deskId, new CardDetail(k.name(), String.valueOf(i)));
                        });
                String studySessionId = this.deckStudyService.startStudySession(deskId);
                studySessionIdMap.put(k, studySessionId);
            });
            logger.info("studySessionIdMap init = {}", studySessionIdMap);

        }
    }

    /**
     * fromRandom时按顺序返题，用于遍历测试题库。
     */
    public static class OrderedFactoryImpl implements IFactory {
        private static final Logger logger = LogManager.getLogger(OrderedFactoryImpl.class.getName());
        public static OrderedFactoryImpl INSTANCE = new OrderedFactoryImpl();
        Integer nextQuizIndex;
        LexiconEnum currentLexiconEnum;
        Iterator<LexiconEnum> lexiconEnumIterator;
        Map<LexiconEnum, Integer> needLoadBookSizeMap;
        @Override
        public BuildQuizDataRequest fromRandom(LexiconEnum lexicon) {
            // 按顺序返题，参数lexicon无意义（为null）
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var result = FactoryUtils.fromTargetIndex(currentLexiconEnum, nextQuizIndex);
            prepareNext();
            return result;
        }

        public boolean hasNext() {
            return currentLexiconEnum != null;
        }

        @Override
        public void afterQuiz(boolean isPerfect) {

        }

        @Override
        public void initMap(Map<LexiconEnum, Integer> needLoadBookSizeMap) {
            this.needLoadBookSizeMap = needLoadBookSizeMap;
            lexiconEnumIterator = needLoadBookSizeMap.keySet().iterator();
            prepareNext();
        }

        /**
         * currentLexiconEnum = null 表示已无下一题；
         * 否则currentLexiconEnum和nextQuizIndex非空。
         */
        private void prepareNext() {
            if (nextQuizIndex != null) {
                nextQuizIndex++;
            }
            while (currentLexiconEnum == null || nextQuizIndex == null || nextQuizIndex >= needLoadBookSizeMap.get(currentLexiconEnum)) {
                if (lexiconEnumIterator.hasNext()) {
                    currentLexiconEnum = lexiconEnumIterator.next();
                    nextQuizIndex = 0;
                } else {
                    currentLexiconEnum = null;
                    nextQuizIndex = null;
                    return;
                }
            }
        }
    }
    public static class FactoryUtils {

        private static int getRandomWordIndex(LexiconEnum lexicon) {
            int size = BookConfig.VOCABULARY_MAP.get(lexicon);
            return MathUtils.random(0, size - 1);
        }

        public static BuildQuizDataRequest fromTargetIndex(LexiconEnum lexicon, int targetId) {
            BuildQuizDataRequest config = BuildQuizDataRequest.builder()
                    .usingLexicon(lexicon)
                    .vocabularySize(BookConfig.VOCABULARY_MAP.get(lexicon))
                    .maxOptionNum(9)
                    .build();
            config.setUiStringsIdStart(CET46Initializer.JSON_MOD_KEY + lexicon.name() + "_");
            config.setTargetId(targetId);
            config.setTargetUiStringsId(config.getUiStringsIdStart() + config.getTargetId());
            return config;
        }
    }


}
