package CET46InSpire.savedata;

import CET46InSpire.relics.CETRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveData {
    private static Logger logger = LogManager.getLogger("CET46SaveData");
    public static final String KEY_ITEMS = "ITEMS";
    public static final String KEY_REMOVED_ITEMS = "REMOVED_ITEMS";
    private static HashMap<String, Integer> items = null;
    private static ArrayList<String> removedItems = null;

    @SpirePatch(clz = SaveFile.class, method = "<ctor>", paramtypez = {SaveFile.SaveType.class})
    public static class SaveTheSaveData {
        @SpirePostfixPatch
        public static void saveAllTheSaveData(SaveFile __instance, SaveFile.SaveType type) {
            CETRelic relic = null;
            for (AbstractRelic r: AbstractDungeon.player.relics) {
                if (r instanceof CETRelic) {
                    relic = (CETRelic) r;
                    break;
                }
            }
            if (relic == null) {
                return;
            }
            SaveData.items = relic.notebook.items;
            SaveData.removedItems = relic.notebook.removedItems;
            SaveData.logger.info("Saved CET data");
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "save", paramtypez = {SaveFile.class})
    public static class SaveDataToFile {
        @SpireInsertPatch(locator = Locator.class, localvars = {"params"})
        public static void addCustomSaveData(SaveFile save, HashMap<Object, Object> params) {
            params.put(SaveData.KEY_ITEMS, SaveData.items);
            params.put(SaveData.KEY_REMOVED_ITEMS, SaveData.removedItems);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(GsonBuilder.class, "create");
                return LineFinder.findInOrder(ctMethodToPatch, (Matcher)methodCallMatcher);
            }
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method = "loadSaveFile", paramtypez = {String.class})
    public static class LoadDataFromFile {
        @SpireInsertPatch(locator = Locator.class, localvars = {"gson", "savestr"})
        public static void loadCustomSaveData(String path, Gson gson, String savestr) {
            try {
                CET46SaveData data = gson.fromJson(savestr, CET46SaveData.class);
                SaveData.items = data.ITEMS;
                SaveData.removedItems = data.REMOVED_ITEMS;
                SaveData.logger.info("Loaded CET46 save data successfully.");
            } catch (Exception e) {
                SaveData.logger.error("Failed to load CET46 save data.");
                e.printStackTrace();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(Gson.class, "fromJson");
                return LineFinder.findInOrder(ctMethodToPatch, (Matcher) methodCallMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "loadSave")
    public static class LoadSave {
        @SpirePostfixPatch
        public static void LoadCETSave(AbstractDungeon __instance, SaveFile file) {
            CETRelic relic = null;
            for (AbstractRelic r: AbstractDungeon.player.relics) {
                if (r instanceof CETRelic) {
                    relic = (CETRelic) r;
                    break;
                }
            }
            if (relic == null) {
                return;
            }
            relic.notebook.items = SaveData.items;
            relic.notebook.removedItems = SaveData.removedItems;
            relic.notebook.resetCount();
            SaveData.logger.info("Save loaded.");
        }
    }
}
