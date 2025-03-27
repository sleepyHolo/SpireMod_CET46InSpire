package CET46InSpire.relics;

import CET46InSpire.actions.CET4QuizAction;
import CET46InSpire.actions.CET6QuizAction;
import CET46InSpire.helpers.ImageElements;
import CET46InSpire.ui.CET46Panel;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BookOfCET6 extends CETRelic {
    public static final String ID = "CET46:BookOfCET6";

    public BookOfCET6() {
        super(ID, ImageElements.RELIC_CET6_IMG, ImageElements.RELIC_CET_OUTLINE,
                RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BookOfCET6();
    }

    @Override
    public void triggerQuiz() {
        flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        if (MathUtils.random(0, 99) < CET46Panel.band4RateIn6) {
            this.addToTop(new CET4QuizAction());
        } else {
            this.addToTop(new CET6QuizAction());
        }
    }
}