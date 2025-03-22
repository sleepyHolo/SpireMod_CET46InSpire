package CET46InSpire.powers;

import CET46InSpire.helpers.ImageElements;
import CET46InSpire.relics.CETRelic;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ChangePowersApplyPower extends AbstractPower {
    public static final String POWER_ID = "CET46:ChangePowersApplyPower";
    private static final PowerStrings powerStrings;
    private static final String NAME;
    private static final String[] DESCRIPTIONS;
    private final CETRelic linkedRelic;

    public ChangePowersApplyPower(AbstractCreature owner, CETRelic linkedRelic) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = AbstractPower.PowerType.BUFF;
        this.linkedRelic = linkedRelic;
        this.amount = -1;
        this.region128 = new TextureAtlas.AtlasRegion(ImageElements.POWER_CET_84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageElements.POWER_CET_32, 0, 0, 32, 32);
        this.updateDescription();
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (this.linkedRelic.counter == 0) {
            this.addToBot(new ReducePowerAction(target, null, power, power.amount));
            return;
        }
        power.amount *= this.linkedRelic.counter;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.linkedRelic.counter + DESCRIPTIONS[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}