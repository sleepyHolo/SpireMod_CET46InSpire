package CET46InSpire.helpers;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BuildQuizDataRequest.FSRSFactory;
import CET46InSpire.relics.QuizRelic;
import CET46InSpire.ui.ModConfigPanel;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BookConfig {
    private static final Logger logger = LogManager.getLogger(BookConfig.class.getName());
    public BookEnum bookEnum;
    public List<LexiconEnum> lexicons;
    public Supplier<QuizRelic> relicSupplier;

    public BookConfig(BookEnum bookEnum, List lexicons, Supplier<QuizRelic> relicSupplier) {
        this.bookEnum = bookEnum;
        // TODO 临时处理一下, 后续需要直接使用List<LexiconEnum>赋值
        this.lexicons = new ArrayList<>();
        for (Object var: lexicons) {
            this.lexicons.add(LexiconEnum.valueOf(var.toString()));
        }
        this.relicSupplier = relicSupplier;
    }

    public boolean needNotLoad() {
        try {
            Field field = ModConfigPanel.class.getField("load" + this.bookEnum.name());
            return !field.getBoolean(null);  // 一定是静态字段
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("bad needNotLoad: ", e);
            return false;
        }
    }

    // --- 代替原 CET46Settings 管理的数据 ---


    /**
     * 含义同CET46Initializer.needLoadBooks
     */
    public static Map<LexiconEnum, Integer> VOCABULARY_MAP = new HashMap<>();

    public static void init_map() {
        // 调用时间必须在Panel初始化后
        CET46Initializer.needLoadBooks.forEach(lexiconEnum -> {
            VOCABULARY_MAP.put(lexiconEnum, Integer.parseInt(CardCrawlGame.languagePack.getUIString(CET46Initializer.JSON_MOD_KEY + lexiconEnum.name() + "_info").TEXT[0]));
        });
        FSRSFactory.INSTANCE.initMap(VOCABULARY_MAP);
        logger.info("CET46Settings init called, VOCABULARY_MAP = {}", VOCABULARY_MAP);
    }


    /**
     * 这个是词库的
     */
    public enum LexiconEnum {
        CET4,
        CET6,
        N5,
        N4,
        N3,
        N2,
        N1
    }

    @Nullable
    public static LexiconEnum getLexicon(String lexicon) {
        for (LexiconEnum l: LexiconEnum.values()) {
            if (l.name().equalsIgnoreCase(lexicon)) {
                return l;
            }
        }
        return null;
    }

}
