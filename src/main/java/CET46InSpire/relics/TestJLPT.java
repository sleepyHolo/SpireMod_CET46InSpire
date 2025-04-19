package CET46InSpire.relics;

import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.events.CallOfCETEvent;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.JapaneseCharacterTool;
import CET46InSpire.helpers.JapaneseKanaConfuser;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestJLPT extends QuizRelic {

    public static final int NORMAL_UISTRINGS_INDEX = 0;
    public static final int MEANING_UISTRINGS_INDEX = 1;
    public static final int KANA_UISTRINGS_INDEX = 2;
    public static final int PLUS_UISTRINGS_INDEX = 3;

    public enum JlptQuizType {
        ASK_KANA,
        ASK_MEANING,
    }

    List<JlptQuizType> activeTypes = Arrays.asList(
            JlptQuizType.ASK_KANA,
            JlptQuizType.ASK_MEANING
    );

    public TestJLPT() {
        super(CallOfCETEvent.BookEnum.JLPT);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TestJLPT();
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
        String meaning = wordUiStrings.TEXT[MEANING_UISTRINGS_INDEX];
        String kana = wordUiStrings.TEXT[KANA_UISTRINGS_INDEX];
        boolean allKanji = JapaneseCharacterTool.isAllKanji(normal.replace("〜", ""));
        JlptQuizType jlptQuizType;
        if (allKanji) {
            jlptQuizType = JlptQuizType.ASK_KANA;
        } else {
            jlptQuizType = JlptQuizType.ASK_MEANING;
        }
        String show = normal;
        List<String> correctOptions = new ArrayList<>();
        List<String> allOptions = new ArrayList<>();
        int choice_num = 3;
        if (choice_num > request.getMaxOptionNum()) {
            choice_num = request.getMaxOptionNum();
        }
        switch (jlptQuizType) {
            case ASK_MEANING:
                correctOptions.add(meaning);
                allOptions.add(meaning);
                // allOptions里填充错误的meaning，来自其他单词
                for (int i = allOptions.size(); i < choice_num;) {
                    int target_word = MathUtils.random(0, request.getVocabularySize() - 1);
                    if (target_word == request.getTargetId()) {
                        continue;
                    }
                    UIStrings otherWord = CardCrawlGame.languagePack.getUIString(request.getUiStringsIdStart() + target_word);
                    String otherWordMeaning = otherWord.TEXT[MEANING_UISTRINGS_INDEX];
                    allOptions.add(otherWordMeaning);
                    i++;
                }
                break;
            case ASK_KANA:
                correctOptions.add(kana);
                allOptions.add(kana);
                // allOptions里填充错误的kana，来自工具
                List<String> confusingList = JapaneseKanaConfuser.generateConfusingKana(kana, choice_num - 1);
                allOptions.addAll(confusingList);
                break;
        }
        return new QuizData(request.targetId, request.getTargetUiStringsId(), show, correctOptions, allOptions);
    }

}
