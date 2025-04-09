package CET46InSpire.relics;

import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.ImageElements;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BookOfJlpt extends CETRelic {
    protected Texture texture;
    public BookOfJlpt(BookEnum bookEnum, Texture texture) {
        super(bookEnum, texture, ImageElements.RELIC_CET_OUTLINE,
                RelicTier.SPECIAL, LandingSound.CLINK);
        this.texture = texture;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BookOfJlpt(bookEnum, texture);
    }

}