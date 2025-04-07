package CET46InSpire.helpers;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.CorrectAction;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.ui.CET46Panel;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 管理：
 * - CET46Panel（被销毁后依然可读取）的数据；
 * - 其他贯穿游戏流程的数据；
 */
public class CET46Settings {
    private static final Logger logger = LogManager.getLogger(CorrectAction.class.getName());
    // ------ CET46Panel的数据 ------
    public static boolean darkMode;
    public static boolean loadCET4;
    public static boolean loadCET6;
    public static boolean loadN5;
    // ------ 其他 ------
    public static Map<BookEnum, Integer> VOCABULARY_MAP = new HashMap<>();

    public static void init() {
        CET46Settings.darkMode = CET46Panel.darkMode;
        CET46Settings.loadCET4 = CET46Panel.loadCET4;
        CET46Settings.loadCET6 = CET46Panel.loadCET6;
        CET46Settings.loadN5 = CET46Panel.loadN5;

        CET46Initializer.needLoadBooks.forEach(bookEnum -> {
            VOCABULARY_MAP.put(bookEnum, Integer.parseInt(CardCrawlGame.languagePack.getUIString(CET46Initializer.JSON_MOD_KEY + bookEnum.name() + "_info").TEXT[0]));
        });
        logger.info("CET46Settings init called");
    }

    public static int getLexiconSize(String lexicon) {
        String key = lexicon.substring(CET46Initializer.JSON_MOD_KEY.length(), lexicon.length() - 1);
        return VOCABULARY_MAP.getOrDefault(BookEnum.valueOf(key), 0);
    }

    public static boolean isLoad(BookEnum b) {
        try {
            Field field = CET46Settings.class.getField("load" + b.name());
            return field.getBoolean(null);  // 一定是静态字段
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("bad isLoad: ", e);
            return false;
        }
    }

}
