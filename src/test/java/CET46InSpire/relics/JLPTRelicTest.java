package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.relics.BuildQuizDataRequest.OrderedFactoryImpl;
import CET46InSpire.relics.IQuizDataStrategy.JlptQuizDataStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

class JLPTRelicTest {
    protected static final Logger logger = LogManager.getLogger(JLPTRelicTest.class.getName());
    BookEnum book = BookEnum.JLPT;

    @Test
    public void runBookCheck() {

        LexiconEnum lexiconEnum = LexiconEnum.N1;
        String fileName = "CET46Resource/vocabulary/N1.json";
        LocalUIStringGetter uiStringGetter = new LocalUIStringGetter(fileName);
        JlptQuizDataStrategy jlptQuizDataStrategy = new JlptQuizDataStrategy(uiStringGetter);
        int size = Integer.parseInt(uiStringGetter.getUIString(CET46Initializer.JSON_MOD_KEY + lexiconEnum.name() + "_info").TEXT[0]);

        // 额外准备环境，被 FactoryUtils 使用
        BookConfig.VOCABULARY_MAP.put(lexiconEnum, size);

        Map<LexiconEnum, Integer> needCheckBookSizeMap = new HashMap<>();
        needCheckBookSizeMap.put(lexiconEnum, size);
        OrderedFactoryImpl orderedFactory = OrderedFactoryImpl.INSTANCE;
        orderedFactory.initMap(needCheckBookSizeMap);
        List<QuizData> badQuizDatas = new ArrayList<>();
        Map<Integer, Integer> optionsNumMap = new HashMap<>();

        int goodQuizCount = 0;
        while (orderedFactory.hasNext()) {
            QuizData quizData = jlptQuizDataStrategy.buildQuizData(orderedFactory.fromRandom(null));
            if (quizData.getAllOptions().size() <= 1) {
                badQuizDatas.add(quizData);
            } else {
                goodQuizCount++;
            }
            optionsNumMap.merge(quizData.getAllOptions().size(), 1, (o1, o2) -> o1 + o2);
        }
        logger.info("runBookCheck needCheckBookSizeMap = {}, goodQuizCount = {}, badQuizCount = {}, optionsNumMap = {}",
                needCheckBookSizeMap, goodQuizCount, badQuizDatas.size(), optionsNumMap);
        if (badQuizDatas.size() > 0) {
            logger.warn("badQuizDatas = {}", badQuizDatas.stream().map(it -> it.getShow()).collect(Collectors.toList()));
        }
    }

}