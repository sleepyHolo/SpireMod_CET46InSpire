package helpers;

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

    public static Texture RELIC_CET4_IMG;
    public static Texture RELIC_CET4_OUTLINE;

    public static void initialize() {
        long startTime = System.currentTimeMillis();

        MOD_BADGE = ImageMaster.loadImage("image/badge.png");
        WORD_SCREEN_BASE = ImageMaster.loadImage("image/ui/word_screen_base.png");
        INFO_BUTTON = ImageMaster.loadImage("image/ui/button.png");
        WORD_BUTTON = ImageMaster.loadImage("image/ui/button_word.png");
        WORD_BUTTON_OUTLINE = ImageMaster.loadImage("image/ui/button_word_outline.png");

        RELIC_CET4_IMG = ImageMaster.loadImage("image/relics/book_of_cet4.png");
        RELIC_CET4_OUTLINE = ImageMaster.loadImage("image/relics/book_of_cet4_outline.png");

        logger.info("Texture load time: {}ms", System.currentTimeMillis() - startTime);
    }

}
