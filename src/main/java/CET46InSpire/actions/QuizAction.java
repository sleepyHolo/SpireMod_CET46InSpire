package CET46InSpire.actions;

import CET46InSpire.ui.CET46Panel;
import basemod.BaseMod;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import CET46InSpire.helpers.ArrayListHelper;
import CET46InSpire.screens.QuizScreen;

import java.util.List;

public abstract class QuizAction extends AbstractGameAction {
    protected final String LEXICON;
    protected final String VOCABULARY_ID;
    protected final int VOCABULARY_SIZE;
    protected static final int MAX_MEANING_NUM;

    public QuizAction(String LEXICON, String VOCABULARY_ID, int VOCABULARY_SIZE) {
        this.LEXICON = LEXICON;
        this.VOCABULARY_ID = VOCABULARY_ID;
        this.VOCABULARY_SIZE = VOCABULARY_SIZE;
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    public QuizAction(String VOCABULARY_ID, int VOCABULARY_SIZE) {
        this("Default", VOCABULARY_ID, VOCABULARY_SIZE);
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER) {

            QuizData quizData = nextQuiz();

            BaseMod.openCustomScreen(QuizScreen.Enum.WORD_SCREEN, quizData.show, LEXICON,
                    quizData.correctOptions, quizData.allOptions, VOCABULARY_ID + quizData.wordId, false);
            tickDuration();
            return;
        }
        tickDuration();

    }

    protected abstract QuizData nextQuiz();

    public static class QuizData {
        public int wordId;
        public String show;
        public List<String> correctOptions;
        public List<String> allOptions;

        public QuizData() {
        }

        public QuizData(int wordId, String show, List<String> correctOptions, List<String> allOptions) {
            this.wordId = wordId;
            this.show = show;
            this.correctOptions = correctOptions;
            this.allOptions = allOptions;
        }
    }

    static {
        MAX_MEANING_NUM = 9;
    }
}
