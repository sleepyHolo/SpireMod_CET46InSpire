package CET46InSpire.patches;

import CET46InSpire.relics.QuizRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

/**
 * 在打出卡牌之前触发小测, 并进行数据修正
 */
public class AbstractPlayerPatch {
    public static AbstractPlayer p = null;
    public static AbstractMonster m = null;
    public static AbstractCard c = null;
    public static int energy = 0;

    /**
     * 这个部分在玩家须接受测验时会终止打出卡牌, 卡牌只有在玩家接受测验后才能打出
     */
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard",
            paramtypez = {AbstractCard.class, AbstractMonster.class, int.class})
    public static class ChangeCardDataPrePlay {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, AbstractCard c, AbstractMonster m, int energyOnUse) {
            for (AbstractRelic r: __instance.relics) {
                if (r instanceof QuizRelic && !((QuizRelic) r).quizzed) {
                    ((QuizRelic) r).sendQuizPrePlay(c);
                    // 记录状态
                    AbstractPlayerPatch.p = __instance;
                    AbstractPlayerPatch.c = c;
                    AbstractPlayerPatch.m = m;
                    AbstractPlayerPatch.energy = energyOnUse;
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        /**
         * 为了修改实际使用的卡牌的数据, 这个部分是必须的, 因为只有传入的 c 才是玩家实际打出的牌
         */
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractPlayer __instance, AbstractCard c, AbstractMonster m, int energyOnUse) {
            for (AbstractRelic r: __instance.relics) {
                if (r instanceof QuizRelic) {
                    ((QuizRelic) r).changeCardPrePlay(c);
                    return;
                }
            }

        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
                return LineFinder.findInOrder(ctBehavior, methodCallMatcher);
            }
        }
    }

}
