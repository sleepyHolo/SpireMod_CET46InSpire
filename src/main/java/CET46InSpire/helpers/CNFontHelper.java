package CET46InSpire.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.core.Settings;
import java.util.HashMap;
import java.util.Objects;

import com.megacrit.cardcrawl.localization.LocalizedStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CNFontHelper {
    private static final Logger logger = LogManager.getLogger(CNFontHelper.class.getName());
    private static FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private static HashMap<String, FreeTypeFontGenerator> generators = new HashMap<>();
    private static FileHandle fontFile = null;
    public static BitmapFont charDescFont;
    public static BitmapFont charTitleFont;
    public static BitmapFont pureDescFont;
    public static BitmapFont pureTitleFont;

    public static void initialize() {
        long startTime = System.currentTimeMillis();
        HashMap<Character, Integer> paramCreator = new HashMap<>();
        if (Objects.requireNonNull(Settings.language) == Settings.GameLanguage.ZHT) {
            fontFile = Gdx.files.internal("font/zht/NotoSansCJKtc-Regular.otf");
        } else {
            // SC
            fontFile = Gdx.files.internal("font/zhs/NotoSansMonoCJKsc-Regular.otf");
        }
        param.borderWidth = 0.0F;
        param.gamma = 0.9F;
        param.borderGamma = 0.9F;
        param.shadowColor = Settings.QUARTER_TRANSPARENT_BLACK_COLOR;
        param.shadowOffsetX = (int)(4.0F * Settings.scale);
        charDescFont = Settings.isMobile ? prepFont(31.0F, false) : prepFont(30.0F, false);
        param.gamma = 1.8F;
        param.borderGamma = 1.8F;
        param.shadowOffsetX = (int)(6.0F * Settings.scale);
        charTitleFont = prepFont(44.0F, false);
        // no shadow
        param.borderWidth = 0.0F;
        param.gamma = 0.9F;
        param.borderGamma = 0.9F;
        param.shadowColor = Settings.QUARTER_TRANSPARENT_BLACK_COLOR;
        param.shadowOffsetX = 0;
        pureDescFont = Settings.isMobile ? prepFont(31.0F, false) : prepFont(30.0F, false);
        param.gamma = 1.8F;
        param.borderGamma = 1.8F;
        param.shadowOffsetX = 0;
        pureTitleFont = prepFont(44.0F, false);

        logger.info("Font load time: {}ms", System.currentTimeMillis() - startTime);
    }

    public static BitmapFont prepFont(float size, boolean isLinearFiltering) {
        FreeTypeFontGenerator g;
        if (generators.containsKey(fontFile.path())) {
            g = generators.get(fontFile.path());
        } else {
            g = new FreeTypeFontGenerator(fontFile);
            generators.put(fontFile.path(), g);
        }
        if (Settings.BIG_TEXT_MODE)
            size *= 1.2F;
        return prepFont(g, size, isLinearFiltering);
    }

    private static BitmapFont prepFont(FreeTypeFontGenerator g, float size, boolean isLinearFiltering) {
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters = "";
        p.incremental = true;
        p.size = Math.round(size * Settings.scale);
        p.gamma = param.gamma;
        p.spaceX = param.spaceX;
        p.spaceY = param.spaceY;
        p.borderColor = param.borderColor;
        p.borderStraight = param.borderStraight;
        p.borderWidth = param.borderWidth;
        p.borderGamma = param.borderGamma;
        p.shadowColor = param.shadowColor;
        p.shadowOffsetX = param.shadowOffsetX;
        p.shadowOffsetY = param.shadowOffsetY;
        if (isLinearFiltering) {
            p.minFilter = Texture.TextureFilter.Linear;
            p.magFilter = Texture.TextureFilter.Linear;
        } else {
            p.minFilter = Texture.TextureFilter.Nearest;
            p.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        }
        g.scaleForPixelHeight(p.size);
        BitmapFont font = g.generateFont(p);
        font.setUseIntegerPositions(!isLinearFiltering);
        (font.getData()).markupEnabled = true;
        if (LocalizedStrings.break_chars != null)
            (font.getData()).breakChars = LocalizedStrings.break_chars.toCharArray();
        (font.getData()).fontFile = fontFile;
        return font;
    }
}
