package CET46InSpire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DiscardAction;

/**
 * 各种原生 STS 行动的补丁, 主要是针对输入为 0 的特殊情况
 */
public class STSActionPatches {

    /**
     * 猎宝弃牌数量为 0 的补丁, 主要针对 Prepared 这张牌
     */
    @SpirePatch(clz = DiscardAction.class, method = "update")
    public static class DiscardActionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(DiscardAction __instance) {
            if (__instance.amount == 0) {
                __instance.isDone = true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

    }
}
