package patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import helpers.ImageElements;

public class ImageMasterPatch {
    @SpirePatch(clz = ImageMaster.class, method = "initialize", paramtypez = {void.class})
    public static class Inst {
        @SpirePostfixPatch
        public static void Postfix() {
            ImageElements.initialize();
        }
    }
}
