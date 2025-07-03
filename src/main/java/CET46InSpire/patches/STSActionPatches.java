package CET46InSpire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.TimeWarpPower;

/**
 * 各种原生 STS 行动的补丁, 主要是针对输入为 0 的特殊情况
 */
public class STSActionPatches {

    /**
     * 所有能力层数 0 的补丁, 如果不打对实际游戏无影响但是会有能力图标
     */
    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class ApplyPowerActionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
            // 检查增加的能力是不是原版能力, 原版能力应该不会出现 0 层(无层数是 -1 层)
            if (__instance.amount == 0 && underPackage(___powerToApply.getClass(), "com.megacrit.cardcrawl.powers")) {
                // 艹了这个补丁把老头能力橄榄了
                if (___powerToApply instanceof TimeWarpPower) {
                    return SpireReturn.Continue();
                }
                __instance.isDone = true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

    }

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

    private static boolean underPackage(Class<?> clazz, String package_) {
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            return package_.isEmpty();
        }
        return pkg.getName().equals(package_) || pkg.getName().startsWith(package_ + ".");
    }
}
