package CET46InSpire;

import CET46InSpire.helpers.CET46Settings;
import CET46InSpire.relics.BookOfCET4;
import CET46InSpire.relics.BookOfCET6;
import CET46InSpire.ui.CET46Panel;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CET46InSpire.screens.QuizScreen;
import CET46InSpire.helpers.ImageElements;

import java.util.Objects;

@SpireInitializer
public class CET46Initializer implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber {
    private static final Logger logger = LogManager.getLogger(CET46Initializer.class.getName());
    public static String MOD_ID = "CET46InSpire";  //MOD_ID必须与ModTheSpire.json中的一致
    public static String CONFIG_UI = "CET46:ConfigPanel";
    private ModPanel settingsPanel = null;

    public CET46Initializer() {
        logger.info("Initialize: {}", MOD_ID);
        BaseMod.subscribe(this);
        settingsPanel = new CET46Panel("config");
    }

    public static void initialize() {
        new CET46Initializer();
    }

    @Override
    public void receiveEditRelics() {
        // cet4
        if (CET46Panel.loadCET4) {
            BaseMod.addRelic(new BookOfCET4(), RelicType.SHARED);
            UnlockTracker.markRelicAsSeen(BookOfCET4.ID);
        }
        if (CET46Panel.loadCET6) {
            BaseMod.addRelic(new BookOfCET6(), RelicType.SHARED);
            UnlockTracker.markRelicAsSeen(BookOfCET6.ID);
        }
    }

    @Override
    public void receiveEditStrings() {
        String lang = "zhs";
        if (Objects.requireNonNull(Settings.language) == Settings.GameLanguage.ZHS) {
            lang = "zhs";
        }

        BaseMod.loadCustomStringsFile(EventStrings.class, "CET46Resource/localization/events_" + lang + ".json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, "CET46Resource/localization/powers_" + lang + ".json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "CET46Resource/localization/relics_" + lang + ".json");
        BaseMod.loadCustomStringsFile(UIStrings.class, "CET46Resource/localization/ui_" + lang + ".json");

        loadVocabulary();
    }

    public void loadVocabulary() {
        long startTime = System.currentTimeMillis();
        if (CET46Panel.loadCET4 || CET46Panel.loadCET6) {
            BaseMod.loadCustomStringsFile(UIStrings.class, "CET46Resource/vocabulary/CET4.json");
        }
        if (CET46Panel.loadCET6) {
            BaseMod.loadCustomStringsFile(UIStrings.class, "CET46Resource/vocabulary/CET6.json");
        }
        logger.info("Vocabulary load time: {}ms", System.currentTimeMillis() - startTime);

    }

    @Override
    public void receivePostInitialize() {
        CET46Settings.init();
        settingsPanel = new CET46Panel();
        BaseMod.registerModBadge(ImageElements.MOD_BADGE,
                "CET46 In Spire", "__name__", "Do_not_forget_CET46!", settingsPanel);

        BaseMod.addCustomScreen(new QuizScreen());
    }

}
