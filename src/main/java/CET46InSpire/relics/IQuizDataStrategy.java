package CET46InSpire.relics;

import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.helpers.JapaneseCharacterTool;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public interface IQuizDataStrategy {
    Logger logger = LogManager.getLogger(IQuizDataStrategy.class.getName());
    /**
     * （已预处理）一定是汉字；若无汉字形式则为null
     */
    int PLUS_UISTRINGS_INDEX = 3;

    QuizData buildQuizData(BuildQuizDataRequest request);

    String WRONG_KANA_KEY = "ConfusedFurigana";
    /**
     * （已预处理）一定是假名
     */
    int KANA_UISTRINGS_INDEX = 2;
    /**
     * 释义
     */
    int MEANING_UISTRINGS_INDEX = 1;
    /**
     * 展示形式；假名或汉字
     */
    int NORMAL_UISTRINGS_INDEX = 0;
    /**
     * 方便脱离Relic进行单元测试
     */
    static class JlptQuizDataStrategy implements IQuizDataStrategy {
        private UIStringGetter uiStringGetter;

        public JlptQuizDataStrategy(UIStringGetter uiStringGetter) {
            this.uiStringGetter = uiStringGetter;
        }

        @Override
        public QuizData buildQuizData(BuildQuizDataRequest request) {
            UIStrings wordUiStrings = uiStringGetter.getUIString(request.getTargetUiStringsId());
            String normal = wordUiStrings.TEXT[NORMAL_UISTRINGS_INDEX];
            boolean showHasKanji = JapaneseCharacterTool.hasAnyKanji(normal.replace("〜", ""));
            List<JlptQuizType> jlptQuizTypes = new ArrayList<>();
            jlptQuizTypes.add(JlptQuizType.ASK_MEANING);
            if (showHasKanji) {
                jlptQuizTypes.add(JlptQuizType.ASK_KANA);
            }
            logger.info("jlptQuizTypes = {}", jlptQuizTypes);
            String show = normal;
            List<String> correctOptions = new ArrayList<>();
            List<String> allOptions = new ArrayList<>();
            jlptQuizTypes.forEach(jlptQuizType -> {
                switch (jlptQuizType) {
                    case ASK_MEANING:
                        addAskMeaning(correctOptions, allOptions, wordUiStrings, 2, request);
                        break;
                    case ASK_KANA:
                        addAskKana(correctOptions, allOptions, wordUiStrings, 2, request);
                        break;
                }
            });
            return new QuizData(request.targetId, request.getTargetUiStringsId(), show, correctOptions, allOptions);
        }


        /**
         * 向correctOptions和allOptions里添加基于Kana的答案;
         * 若无法生成错误答案，则放弃添加，这两list不变；
         * @param choice_num 该方法最多生成答案数
         */
        private void addAskKana(List<String> correctOptions, List<String> allOptions, UIStrings wordUiStrings, int choice_num, BuildQuizDataRequest request) {
            String kana = wordUiStrings.TEXT[KANA_UISTRINGS_INDEX];
            List<String> wrongKanaList = Optional.ofNullable(wordUiStrings.TEXT_DICT)
                    .map(it -> it.get(WRONG_KANA_KEY))
                    .map(it -> Arrays.asList(it.split("\\|")))
                    .orElseGet(() -> new ArrayList<>(0));
            List<String> confusingList = new ArrayList<>();
            confusingList.add(kana);
            int size = Math.min(choice_num, request.getMaxOptionNum() - allOptions.size());
            if (wrongKanaList.size() >= size) {
                confusingList.addAll(wrongKanaList.subList(0, size));
            }
            if (confusingList.size() > 1) {
                correctOptions.add(kana);
                Collections.shuffle(confusingList);
                allOptions.addAll(confusingList);
            }
        }

        /**
         * 向correctOptions和allOptions里添加基于Meaning的答案;
         * 若无法生成错误答案，则放弃添加，这两list不变；
         * @param choice_num 该方法最多生成答案数
         */
        private void addAskMeaning(List<String> correctOptions, List<String> allOptions, UIStrings wordUiStrings, int choice_num, BuildQuizDataRequest request) {
            String meaning = wordUiStrings.TEXT[MEANING_UISTRINGS_INDEX];

            List<String> confusingList = new ArrayList<>();
            confusingList.add(meaning);
            // allOptions里填充错误的meaning，来自其他单词
            for (int i = 0; i < choice_num && allOptions.size() < request.getMaxOptionNum(); i++) {
                int otherWordId = MathUtils.random(0, request.getVocabularySize() - 1);
                if (otherWordId == request.getTargetId()) {
                    continue;
                }
                UIStrings otherWord = uiStringGetter.getUIString(request.getUiStringsIdStart() + otherWordId);
                String otherWordMeaning = otherWord.TEXT[MEANING_UISTRINGS_INDEX];
                confusingList.add(otherWordMeaning);
            }
            Collections.shuffle(confusingList);
            if (confusingList.size() > 1) {
                correctOptions.add(meaning);
                allOptions.addAll(confusingList);
            }
        }

    };
}
