package CET46InSpire.savedata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.exceptions.SaveFileLoadError;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveAndLoad {
    private static final Logger logger = LogManager.getLogger(SaveAndLoad.class.getName());
    public static final String SAVE_PATH;

    public static String getSavePath(String lexicon) {
        StringBuilder sb = new StringBuilder();
        sb.append(SAVE_PATH);
        if (lexicon.isEmpty()) {
            sb.append("CETNotebook.save");
        } else {
            sb.append(lexicon);
            if (CardCrawlGame.saveSlot != 0)
                sb.append(CardCrawlGame.saveSlot).append("_");
            sb.append(".autosave");
        }
        return sb.toString();
    }

    public static String getSavePath() {
        return SaveAndLoad.getSavePath("");
    }

//    public static boolean saveExistsAndNotCorrupted(String lexicon) {
//        String filepath = getSavePath(lexicon);
//        boolean fileExists = Gdx.files.local(filepath).exists();
//        if (fileExists) {
//            try {
//                loadSaveFile(filepath);
//            } catch (SaveFileLoadError saveFileLoadError) {
//                deleteSave(lexicon);
//                logger.info(p.chosenClass.name() + " save INVALID!");
//                return false;
//            }
//            logger.info(p.chosenClass.name() + " save exists and is valid.");
//            return true;
//        }
//        logger.info(p.chosenClass.name() + " save does NOT exist!");
//        return false;
//    }

    private static String loadSaveString(String filePath) {
        FileHandle file = Gdx.files.local(filePath);
        String data = file.readString();
        return data;
    }

    private static CorrectionNote loadSaveFile(String filePath) throws SaveFileLoadError {
        CorrectionNote note = null;
        Gson gson = new Gson();
        String savestr = null;
        try {
            savestr = loadSaveString(filePath);
            note = (CorrectionNote)gson.fromJson(savestr, CorrectionNote.class);
        } catch (Exception e) {
            throw new SaveFileLoadError("Unable to load save file: " + filePath, e);
        }
        logger.info(filePath + " save file was successfully loaded.");
        return note;
    }

    static {
        SAVE_PATH = SaveAndContinue.SAVE_PATH;
    }
}
