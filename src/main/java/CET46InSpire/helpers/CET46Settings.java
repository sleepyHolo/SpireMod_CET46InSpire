package CET46InSpire.helpers;

import CET46InSpire.ui.CET46Panel;

public class CET46Settings {
    public static boolean darkMode = false;
    public static boolean CET4Available = true;

    public static void init() {
        CET46Settings.darkMode = CET46Panel.darkMode;
        CET46Settings.CET4Available = CET46Panel.loadCET4;
    }
}
