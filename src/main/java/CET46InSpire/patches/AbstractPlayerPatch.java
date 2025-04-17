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
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard",
            paramtypez = {AbstractCard.class, AbstractMonster.class, int.class})
    public static class ChangeCardDataPrePlay {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, AbstractCard c, AbstractMonster m, int energyOnUse) {
            for (AbstractRelic r: __instance.relics) {
                if (r instanceof QuizRelic) {
                    ((QuizRelic) r).sendQuizPrePlay();
                    return;
                }
            }

        }

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
