package CET46InSpire.powers;

import CET46InSpire.helpers.ImageElements;
import CET46InSpire.relics.CETRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.TheBombPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.lang.reflect.Field;

public class ChangePowersApplyPower extends AbstractPower {
    public static final String POWER_ID = "CET46:ChangePowersApplyPower";
    private static final PowerStrings powerStrings;
    private static final String NAME;
    private static final String[] DESCRIPTIONS;
    private final CETRelic linkedRelic;
    private final Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private boolean oddTurn = true;

    public ChangePowersApplyPower(AbstractCreature owner, CETRelic linkedRelic) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = AbstractPower.PowerType.BUFF;
        this.linkedRelic = linkedRelic;
        this.amount = linkedRelic.counter;
        this.region128 = new TextureAtlas.AtlasRegion(ImageElements.POWER_CET_84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageElements.POWER_CET_32, 0, 0, 32, 32);
        this.updateDescription();
        this.oddTurn = true;
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (source != AbstractDungeon.player) {
            return;
        }
        if (this.linkedRelic.scoreCounter == 0) {
            this.addToBot(new ReducePowerAction(target, null, power, power.amount));
            return;
        }
        for (int i = 1; i < this.linkedRelic.scoreCounter; i++) {
            if (power instanceof TheBombPower) {
                // new bomb power
                try {
                    Field ___damage = TheBombPower.class.getDeclaredField("damage");
                    ___damage.setAccessible(true);
                    this.addToBot(new ApplyPowerAction(target, null,
                            new TheBombPower(target, power.amount, ___damage.getInt(power)), power.amount));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            this.addToBot(new ApplyPowerAction(target, null, power, power.amount));
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.oddTurn = !this.oddTurn;
        if (this.amount >= 5 && this.oddTurn) {
            for (AbstractMonster target: AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (target.isDeadOrEscaped()) {
                    continue;
                }
                this.addToBot(new ApplyPowerAction(target, null,
                        new WeakPower(target, 1, false), 1, true));
            }
        }
        if (this.amount >= 10 && !this.oddTurn) {
            for (AbstractMonster target: AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (target.isDeadOrEscaped()) {
                    continue;
                }
                this.addToBot(new ApplyPowerAction(target, null,
                        new VulnerablePower(target, 1, false), 1, true));
            }
        }
    }

    public void updatePerfectCounter() {
        this.amount = linkedRelic.counter;
    }

    @Override
    public void updateDescription() {
        String tmp = DESCRIPTIONS[0] + this.linkedRelic.scoreCounter + DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        tmp += makePowerTag(5, this.amount >= 5, 3);
        tmp += makePowerTag(10, this.amount >= 10, 4);
        this.description = tmp;
    }

    private String makePowerTag(int number, boolean isActive, int index) {
        if (isActive) {
            return " NL #y[" + number + "+] " + DESCRIPTIONS[index];
        }
        return "";
    }

    @Override
    public void stackPower(int stackAmount) {
        return;
    }

    @Override
    public void reducePower(int reduceAmount) {
        return;
    }

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        if (!this.isTurnBased) {
            this.color.a = c.a;
            c = this.color;
        }
        FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, Integer.toString(this.amount), x, y, this.fontScale, c);
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}