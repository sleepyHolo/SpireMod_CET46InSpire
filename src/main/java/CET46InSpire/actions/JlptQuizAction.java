package CET46InSpire.actions;

import CET46InSpire.CET46Initializer;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.helpers.JapaneseCharacterTool;
import CET46InSpire.helpers.JapaneseKanaConfuser;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JlptQuizAction extends QuizAction {

    public JlptQuizAction(BookConfig bookConfig, LexiconEnum usingLexicon) {
        super(
                bookConfig.bookEnum.name(),
                CET46Initializer.JSON_MOD_KEY + usingLexicon.name() + "_",
                BookConfig.VOCABULARY_MAP.get(usingLexicon)
        );
    }

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

    @Override
    protected QuizData nextQuiz() {
        int wordId = MathUtils.random(0, VOCABULARY_SIZE - 1);
        UIStrings wordUiStrings = CardCrawlGame.languagePack.getUIString(VOCABULARY_ID + wordId);
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
        if (choice_num > MAX_MEANING_NUM) {
            choice_num = MAX_MEANING_NUM;
        }
        switch (jlptQuizType) {
            case ASK_MEANING:
                correctOptions.add(meaning);
                allOptions.add(meaning);
                // allOptions里填充错误的meaning，来自其他单词
                for (int i = allOptions.size(); i < choice_num;) {
                    int target_word = MathUtils.random(0, VOCABULARY_SIZE - 1);
                    if (target_word == wordId) {
                        continue;
                    }
                    UIStrings otherWord = CardCrawlGame.languagePack.getUIString(VOCABULARY_ID + target_word);
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
        return new QuizData(wordId, show, correctOptions, allOptions);
    }
}
