package CET46InSpire.relics;

import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.events.CallOfCETEvent;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.helpers.JapaneseCharacterTool;
import CET46InSpire.helpers.JapaneseKanaConfuser;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.*;

public class JLPTRelic extends QuizRelic {

    /**
     * 展示形式；假名或汉字
     */
    public static final int NORMAL_UISTRINGS_INDEX = 0;
    /**
     * 释义
     */
    public static final int MEANING_UISTRINGS_INDEX = 1;
    /**
     * （已预处理）一定是假名
     */
    public static final int KANA_UISTRINGS_INDEX = 2;
    /**
     * （已预处理）一定是汉字；若无汉字形式则为null
     */
    public static final int PLUS_UISTRINGS_INDEX = 3;
    public static final String WRONG_KANA_KEY = "ConfusedFurigana";
    public enum JlptQuizType {
        ASK_KANA,
        ASK_MEANING,
    }

    List<JlptQuizType> activeTypes = Arrays.asList(
            JlptQuizType.ASK_KANA,
            JlptQuizType.ASK_MEANING
    );

    public JLPTRelic() {
        super(CallOfCETEvent.BookEnum.JLPT);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new JLPTRelic();
    }

    @Override
    public String updateDesByLexicon(BookConfig.LexiconEnum lexiconEnum) {
        if (lexiconEnum == null) {
            return "NULL";
        }
        switch (lexiconEnum) {
            case N1:
                return DESCRIPTIONS[0];
            case N2:
                return DESCRIPTIONS[1];
            case N3:
                return DESCRIPTIONS[2];
            case N4:
                return DESCRIPTIONS[3];
            case N5:
                return DESCRIPTIONS[4];
        }
        return "???";
    }

    @Override
    public QuizData buildQuizData(BuildQuizDataRequest request) {
        UIStrings wordUiStrings = CardCrawlGame.languagePack.getUIString(request.getTargetUiStringsId());
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
        confusingList.addAll(wrongKanaList.subList(0, size));
        if (confusingList.size() > 1) {
            correctOptions.add(kana);
            Collections.shuffle(confusingList);
            allOptions.addAll(confusingList);
        }
    }

    /**
     * 向correctOptions和allOptions里添加基于Meaning的答案;
     * 若无法生成错误答案，则放弃添加，这两list不变；
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
            UIStrings otherWord = CardCrawlGame.languagePack.getUIString(request.getUiStringsIdStart() + otherWordId);
            String otherWordMeaning = otherWord.TEXT[MEANING_UISTRINGS_INDEX];
            confusingList.add(otherWordMeaning);
        }
        Collections.shuffle(confusingList);
        if (confusingList.size() > 1) {
            correctOptions.add(meaning);
            allOptions.addAll(confusingList);
        }
    }
}
