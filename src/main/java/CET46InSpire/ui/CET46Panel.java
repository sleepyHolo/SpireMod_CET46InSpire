package CET46InSpire.ui;

import CET46InSpire.CET46Initializer;
import basemod.EasyConfigPanel;

public class CET46Panel extends EasyConfigPanel {
    public static boolean fastMode = false;
    public static boolean ignoreCheck = false;
    public static boolean loadCET4 = true;

    public CET46Panel(String configName) {
        super(CET46Initializer.MOD_ID, null, configName);
    }

    public CET46Panel() {
        super(CET46Initializer.MOD_ID, CET46Initializer.CONFIG_UI);
    }

}
