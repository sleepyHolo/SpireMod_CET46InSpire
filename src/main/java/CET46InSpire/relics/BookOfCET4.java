package CET46InSpire.relics;

import CET46InSpire.actions.CET4QuizAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import CET46InSpire.helpers.ImageElements;

public class BookOfCET4 extends CETRelic {
    public static final String ID = "CET46:BookOfCET4";

    public BookOfCET4() {
        super(ID, ImageElements.RELIC_CET4_IMG, ImageElements.RELIC_CET_OUTLINE,
                RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BookOfCET4();
    }

    @Override
    public void triggerQuiz() {
        flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToTop(new CET4QuizAction());
    }
}