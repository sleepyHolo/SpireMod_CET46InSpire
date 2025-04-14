package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.List;

public abstract class QuizRelic extends AbstractRelic {
    private static BookEnum book;
    private static List<LexiconEnum> lexicons;
    static {
        lexicons = new ArrayList<>();
    }

    public QuizRelic(BookEnum b, RelicTier tier, LandingSound sfx) {
        super(toId(b), "", tier, sfx);
        book = b;
    }


    public static String toId(BookEnum b) {
        return CET46Initializer.JSON_MOD_KEY + b.name() + "_relic";
    }

}
