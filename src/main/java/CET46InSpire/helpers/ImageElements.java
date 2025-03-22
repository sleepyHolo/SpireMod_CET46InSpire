package CET46InSpire.helpers;

import CET46InSpire.ui.CET46Panel;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageElements {
    private static final Logger logger = LogManager.getLogger(ImageElements.class.getName());
    public static Texture MOD_BADGE;
    public static Texture WORD_SCREEN_BASE;
    public static Texture INFO_BUTTON;
    public static Texture WORD_BUTTON;
    public static Texture WORD_BUTTON_OUTLINE;
    public static Texture INFO_TIP;

    public static Texture RELIC_CET4_IMG;
    public static Texture RELIC_CET4_OUTLINE;

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
        RELIC_CET4_OUTLINE = ImageMaster.loadImage("CET46Resource/image/relics/book_of_cet4_outline.png");

        logger.info("Texture load time: {}ms", System.currentTimeMillis() - startTime);
    }

}
