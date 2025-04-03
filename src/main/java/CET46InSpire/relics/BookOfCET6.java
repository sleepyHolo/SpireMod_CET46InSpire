package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.GeneralQuizAction;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.ImageElements;
import CET46InSpire.ui.CET46Panel;
import CET46InSpire.ui.CET46Panel.BookConfig;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BookOfCET6 extends CETRelic {

    public BookOfCET6() {
        super(BookEnum.CET6, ImageElements.RELIC_CET6_IMG, ImageElements.RELIC_CET_OUTLINE,
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

}