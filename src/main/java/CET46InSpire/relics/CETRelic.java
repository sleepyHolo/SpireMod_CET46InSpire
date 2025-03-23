package CET46InSpire.relics;

import CET46InSpire.powers.ChangePowersApplyPower;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CETRelic extends CustomRelic {
    private static final Logger logger = LogManager.getLogger(CETRelic.class.getName());
    public static final int VOCABULARY_CET4;
    public static final int VOCABULARY_CET6;
    public int pre_counter;

    public CETRelic(String id, Texture texture, Texture outline, RelicTier tier, LandingSound sfx) {
        super(id, texture, outline, tier, sfx);
        this.counter = 1;   // score
        this.pre_counter = this.counter;
    }

    @Override
    public void update() {
        super.update();
        if (this.pre_counter != this.counter) {
            this.pre_counter = this.counter;
            AbstractDungeon.player.getPower(ChangePowersApplyPower.POWER_ID).updateDescription();
        }
    }

    @Override
    public void atBattleStartPreDraw() {
        this.flash();
        this.addToTop(new ApplyPowerAction(AbstractDungeon.player, null,
                new ChangePowersApplyPower(AbstractDungeon.player, this)));
    }

    @Override
    public int onPlayerGainedBlock(float blockAmount) {
        return MathUtils.floor(this.counter * blockAmount);
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        return this.counter * damageAmount;
    }

    @Override
    public void onPlayerEndTurn() {
        this.counter = 1;
    }

    @Override
    public void onVictory() {
        this.counter = 1;
    }

    static {
        VOCABULARY_CET4 = Integer.parseInt(CardCrawlGame.languagePack.getUIString("CET46:CET4_info").TEXT[0]);
        VOCABULARY_CET6 = Integer.parseInt(CardCrawlGame.languagePack.getUIString("CET46:CET6_info").TEXT[0]);
    }
}
