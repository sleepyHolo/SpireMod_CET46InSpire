package CET46InSpire.patches;

import CET46InSpire.helpers.CNFontHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class FontHelperPatch {
    @SpirePatch(clz = FontHelper.class, method = "initialize", paramtypez = {void.class})
    public static class Inst {
        @SpirePostfixPatch
        public static void Postfix() {
            CNFontHelper.initialize();
        }
    }
}
