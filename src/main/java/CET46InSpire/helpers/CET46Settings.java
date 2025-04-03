package CET46InSpire.helpers;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.ui.CET46Panel;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.*;

public class CET46Settings {
    public static boolean darkMode = false;

    public static Map<BookEnum, Integer> VOCABULARY_MAP = new HashMap<>();

    public static void init() {
        CET46Settings.darkMode = CET46Panel.darkMode;


        CET46Initializer.needLoadBooks.forEach(bookEnum -> {
            VOCABULARY_MAP.put(bookEnum, Integer.parseInt(CardCrawlGame.languagePack.getUIString(CET46Initializer.JSON_MOD_KEY + bookEnum.name() + "_info").TEXT[0]));
        });

    }

    public static int getLexiconSize(String lexicon) {
        String key = lexicon.substring(CET46Initializer.JSON_MOD_KEY.length(), lexicon.length() - 1);
        return VOCABULARY_MAP.getOrDefault(BookEnum.valueOf(key), 0);
    }

}
