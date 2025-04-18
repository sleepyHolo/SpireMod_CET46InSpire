package CET46InSpire.actions;

import CET46InSpire.patches.AbstractPlayerPatch;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import CET46InSpire.screens.QuizScreen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class QuizAction extends AbstractGameAction {
    private static final Logger logger = LogManager.getLogger(AbstractPlayerPatch.class);
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
            logger.info("quizData = {}", quizData);
            BaseMod.openCustomScreen(QuizScreen.Enum.WORD_SCREEN, quizData.show, LEXICON,
                    quizData.correctOptions, quizData.allOptions, VOCABULARY_ID + quizData.wordId, false);
            tickDuration();
            return;
        }
        tickDuration();

    }

    protected abstract QuizData nextQuiz();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuizData {
        private int wordId;
        private String show;
        private List<String> correctOptions;
        private List<String> allOptions;
    }

    static {
        MAX_MEANING_NUM = 9;
    }
}
