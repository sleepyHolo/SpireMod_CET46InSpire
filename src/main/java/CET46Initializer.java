import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.abstracts.CustomRelic;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import events.CallOfCETEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import screens.QuizScreen;
import helpers.ImageElements;

import java.util.Objects;

@SpireInitializer
public class CET46Initializer implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber {
    private static final Logger logger = LogManager.getLogger(CET46Initializer.class.getName());
    public static String MOD_ID = "CET46InSpire";  //MOD_ID必须与ModTheSpire.json中的一致

    public CET46Initializer() {
        logger.info("Initialize: {}", MOD_ID);
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new CET46Initializer();
    }

    @Override
    public void receiveEditRelics() {
        new AutoAdd(MOD_ID)
                .packageFilter("relics")
                .any(CustomRelic.class, (info, relic) -> {
                    BaseMod.addRelic(relic, RelicType.SHARED);
                    if (info.seen) {
                        UnlockTracker.markRelicAsSeen(relic.relicId);
                    }
                });
    }

    @Override
    public void receiveEditStrings() {
        String lang = "zhs";
        if (Objects.requireNonNull(Settings.language) == Settings.GameLanguage.ZHS) {
            lang = "zhs";
        }

        BaseMod.loadCustomStringsFile(EventStrings.class, "localization/events_" + lang + ".json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, "localization/relics_" + lang + ".json");
        BaseMod.loadCustomStringsFile(UIStrings.class, "localization/ui_" + lang + ".json");

        long startTime = System.currentTimeMillis();
        BaseMod.loadCustomStringsFile(UIStrings.class, "vocabulary/CET4.json");
        logger.info("Vocabulary load time: {}ms", System.currentTimeMillis() - startTime);
    }

    @Override
    public void receivePostInitialize() {
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(ImageElements.MOD_BADGE,
                "CET46 In Spire", "__name__", "Do_not_forget_CET46!", settingsPanel);

        BaseMod.addCustomScreen(new QuizScreen());
        // debug only
//        BaseMod.addEvent(CallOfCETEvent.ID, CallOfCETEvent.class);
    }

}
