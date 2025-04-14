package CET46InSpire.helpers;

import CET46InSpire.ui.CET46Panel;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class ImageElements {
    private static final Logger logger = LogManager.getLogger(ImageElements.class.getName());
    public static Texture MOD_BADGE;

    /**
     * UI界面图片
     */
    public static Texture WORD_SCREEN_BASE;
    public static Texture INFO_BUTTON;
    public static Texture WORD_BUTTON;
    public static Texture WORD_BUTTON_OUTLINE;
    public static Texture INFO_TIP;

    /**
     * 游戏内元素的图片
     * 遗物 & 能力
     */
    public static Texture RELIC_CET4_IMG;
    public static Texture RELIC_CET6_IMG;
    public static Texture RELIC_N5_IMG;
    public static Texture RELIC_CET_OUTLINE;
    public static Texture POWER_CET_32;
    public static Texture POWER_CET_84;

    /**
     * 原CET46Settings配置, 因为是图片相关就移到这个类了
     */
    public static boolean darkMode;

    public static void initialize() {
        long startTime = System.currentTimeMillis();

        MOD_BADGE = ImageMaster.loadImage("CET46Resource/image/badge.png");
        if (CET46Panel.darkMode) {
            WORD_SCREEN_BASE = ImageMaster.loadImage("CET46Resource/image/ui/word_screen_base_dark.png");
            INFO_BUTTON = ImageMaster.loadImage("CET46Resource/image/ui/button_dark.png");
            WORD_BUTTON = ImageMaster.loadImage("CET46Resource/image/ui/button_word_dark.png");
            INFO_TIP = ImageMaster.loadImage("CET46Resource/image/ui/info_tip_dark.png");
        } else {
            WORD_SCREEN_BASE = ImageMaster.loadImage("CET46Resource/image/ui/word_screen_base.png");
            INFO_BUTTON = ImageMaster.loadImage("CET46Resource/image/ui/button.png");
            WORD_BUTTON = ImageMaster.loadImage("CET46Resource/image/ui/button_word.png");
            INFO_TIP = ImageMaster.loadImage("CET46Resource/image/ui/info_tip.png");
        }
        WORD_BUTTON_OUTLINE = ImageMaster.loadImage("CET46Resource/image/ui/button_word_outline.png");

        RELIC_CET4_IMG = ImageMaster.loadImage("CET46Resource/image/relics/book_of_cet4.png");
        RELIC_CET6_IMG = ImageMaster.loadImage("CET46Resource/image/relics/book_of_cet6.png");
        RELIC_N5_IMG = ImageMaster.loadImage("CET46Resource/image/relics/book_of_n5.png");
        RELIC_CET_OUTLINE = ImageMaster.loadImage("CET46Resource/image/relics/book_of_cet_outline.png");

        POWER_CET_32 = ImageMaster.loadImage("CET46Resource/image/powers/cet_power_32.png");
        POWER_CET_84 = ImageMaster.loadImage("CET46Resource/image/powers/cet_power_84.png");

        logger.info("Texture load time: {}ms", System.currentTimeMillis() - startTime);

        ImageElements.darkMode = CET46Panel.darkMode;
    }

    public static Texture getLexiconTexture(BookConfig.LexiconEnum l) {
        try {
            Field field = ImageElements.class.getField("RELIC_" + l.name() + "_IMG");
            return (Texture) field.get(null);
        } catch (Exception e) {
            logger.info("No texture for lexicon: {}, err: {}", l.name(), e);
            return RELIC_CET4_IMG;
        }
    }

}
