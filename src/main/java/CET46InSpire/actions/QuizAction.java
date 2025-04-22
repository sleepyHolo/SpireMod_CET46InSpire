package CET46InSpire.actions;

import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.relics.BuildQuizDataRequest;
import CET46InSpire.relics.QuizRelic;
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
    private static final Logger logger = LogManager.getLogger(QuizAction.class);
    protected final LexiconEnum lexicon;
    private final QuizRelic quizRelic;
    public QuizAction(QuizRelic quizRelic, LexiconEnum lexicon) {
        this.lexicon = lexicon;
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
        this.quizRelic = quizRelic;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER) {
            QuizData quizData = quizRelic.buildQuizData(BuildQuizDataRequest.Factory.fromRandom(lexicon));
            logger.info("quizData = {}", quizData);
            BaseMod.openCustomScreen(QuizScreen.Enum.WORD_SCREEN, quizData.show, lexicon.name(),
                    quizData.correctOptions, quizData.allOptions, quizData.getWordUiStringsId(), false);
            tickDuration();
            return;
        }
        tickDuration();

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuizData {
        private int wordId;
        private String wordUiStringsId;
        public String show;
        public List<String> correctOptions;
        public List<String> allOptions;
    }

}
