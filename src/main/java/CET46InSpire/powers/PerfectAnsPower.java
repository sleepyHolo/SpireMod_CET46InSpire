package CET46InSpire.powers;

import CET46InSpire.helpers.ImageElements;
import CET46InSpire.relics.QuizRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class PerfectAnsPower extends AbstractPower {
    public static final String POWER_ID = "CET46:PerfectAnsPower";
    private static final PowerStrings powerStrings;
    private static final String NAME;
    private static final String[] DESCRIPTIONS;
    private final QuizRelic linkedRelic;
    private final Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private boolean oddTurn = true;

    public PerfectAnsPower(AbstractCreature owner, QuizRelic linkedRelic) {
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

    @Override
    public void updateDescription() {
        this.amount = linkedRelic.counter;
        String tmp = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
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