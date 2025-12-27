package CET46InSpire.actions;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.relics.QuizRelic;
import CET46InSpire.relics.BuildQuizDataRequest;
import CET46InSpire.screens.QuizScreen;
import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CorrectAction extends AbstractGameAction {
    private static final Logger logger = LogManager.getLogger(CorrectAction.class.getName());
    private static final String LEXICON;
    private final int target_id;
    private final LexiconEnum lexicon;
    private final QuizRelic quizRelic;
    public CorrectAction(String target, QuizRelic quizRelic) {
        String[] tmp = target.split("_");
        this.target_id = Integer.parseInt(tmp[1]);
        String key = tmp[0].substring(CET46Initializer.JSON_MOD_KEY.length());
        this.lexicon = LexiconEnum.valueOf(key);
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
        this.quizRelic = quizRelic;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER) {
            QuizData quizData = quizRelic.buildQuizData(BuildQuizDataRequest.Factory.fromTargetIndex(lexicon, target_id));
            logger.info("quizData = {}", quizData);
            BaseMod.openCustomScreen(QuizScreen.Enum.WORD_SCREEN, quizData.getShow(), LEXICON,
                    quizData.getCorrectOptions(), quizData.getAllOptions(), quizData.getWordUiStringsId(), true);
            tickDuration();
            return;
        }
        tickDuration();
    }

    static {
        LEXICON = CardCrawlGame.languagePack.getUIString("CET46:WordScreen").TEXT[4];
    }

}
