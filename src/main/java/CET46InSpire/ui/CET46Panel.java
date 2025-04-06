package CET46InSpire.ui;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import basemod.EasyConfigPanel;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

public class CET46Panel extends EasyConfigPanel {
    public static boolean darkMode = false;
    public static boolean pureFont = true;
    public static boolean fastMode = false;
    public static boolean casualMode = false;
    public static boolean ignoreCheck = false;
    public static boolean showLexicon = true;
    public static int band4RateIn6 = 50;
    public static int maxAnsNum = 3;
    public static boolean loadCET4 = true;
    public static boolean loadCET6 = true;
    public static boolean loadN5 = false;


    public CET46Panel(String configName) {
        super(CET46Initializer.MOD_ID, null, configName);
    }

    public CET46Panel() {
        super(CET46Initializer.MOD_ID, CET46Initializer.JSON_MOD_KEY + "ConfigPanel");
        setNumberRange("band4RateIn6", 0, 80);
        setNumberRange("maxAnsNum", 1, 3);

        setPadding(2.0F);
    }

}
