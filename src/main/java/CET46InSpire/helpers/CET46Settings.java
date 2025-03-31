package CET46InSpire.helpers;

import CET46InSpire.ui.CET46Panel;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class CET46Settings {
    public static boolean darkMode = false;
    public static boolean CET4Available = true;
    public static boolean CET6Available = true;

    public static int VOCABULARY_CET4 = 0;
    public static int VOCABULARY_CET6 = 0;

    public static void init() {
        CET46Settings.darkMode = CET46Panel.darkMode;
        CET46Settings.CET4Available = CET46Panel.loadCET4;
        CET46Settings.CET6Available = CET46Panel.loadCET6;

        if (CET46Panel.loadCET4 || CET46Panel.loadCET6) {
            CET46Settings.VOCABULARY_CET4 = Integer.parseInt(CardCrawlGame.languagePack.getUIString("CET46:CET4_info").TEXT[0]);
        }
        if (CET46Panel.loadCET6) {
            CET46Settings.VOCABULARY_CET6 = Integer.parseInt(CardCrawlGame.languagePack.getUIString("CET46:CET6_info").TEXT[0]);
        }
    }

    public static int getLexiconSize(String lexicon) {
        switch (lexicon) {
            case "CET46:CET4_":
                return CET46Settings.VOCABULARY_CET4;
            case "CET46:CET6_":
                return CET46Settings.VOCABULARY_CET6;
            default:
                return 0;
        }
    }

}
