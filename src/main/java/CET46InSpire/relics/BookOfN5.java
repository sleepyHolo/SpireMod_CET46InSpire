package CET46InSpire.relics;

import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.ImageElements;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BookOfN5 extends CETRelic {

    public BookOfN5() {
        super(BookEnum.N5, ImageElements.RELIC_CET6_IMG, ImageElements.RELIC_CET_OUTLINE,
                RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BookOfN5();
    }

}