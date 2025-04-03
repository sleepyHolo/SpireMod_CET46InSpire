package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.GeneralQuizAction;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import CET46InSpire.helpers.ImageElements;

public class BookOfCET4 extends CETRelic {

    public BookOfCET4() {
        super(BookEnum.CET4, ImageElements.RELIC_CET4_IMG, ImageElements.RELIC_CET_OUTLINE,
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

}