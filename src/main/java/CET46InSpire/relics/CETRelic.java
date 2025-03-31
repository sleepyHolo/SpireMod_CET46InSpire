package CET46InSpire.relics;

import CET46InSpire.actions.CorrectAction;
import CET46InSpire.powers.ChangePowersApplyPower;
import CET46InSpire.savedata.CorrectionNote;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class CETRelic extends CustomRelic implements ClickableRelic {
    public int pre_counter;
    public int scoreCounter = -1;
    public CorrectionNote notebook = null;

    public CETRelic(String id, Texture texture, Texture outline, RelicTier tier, LandingSound sfx) {
        super(id, texture, outline, tier, sfx);
        this.counter = 0;   // perfect counter
        this.scoreCounter = 1;
        this.pre_counter = this.scoreCounter;
        this.notebook = new CorrectionNote();
    }

    public void updatePerfectCounter(boolean isPerfect) {
        if (isPerfect) {
            this.counter++;
        } else {
            this.counter = 0;
        }
        ((ChangePowersApplyPower) AbstractDungeon.player.getPower(ChangePowersApplyPower.POWER_ID)).updatePerfectCounter();
        AbstractDungeon.player.getPower(ChangePowersApplyPower.POWER_ID).updateDescription();
    }

    @Override
    public void update() {
        super.update();
        if (this.pre_counter != this.scoreCounter) {
            this.pre_counter = this.scoreCounter;
            AbstractDungeon.player.getPower(ChangePowersApplyPower.POWER_ID).updateDescription();
        }
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
        if (this.counter > -1) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.counter),
                    this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.WHITE);
        }
        if (this.scoreCounter > -1) {
            FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(this.scoreCounter),
                    this.currentX + 30.0F * Settings.scale, this.currentY + 24.0F * Settings.scale, Color.WHITE);
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
        return MathUtils.floor(this.scoreCounter * blockAmount);
    }

    @Override
    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        return this.scoreCounter * damageAmount;
    }

    @Override
    public void onPlayerEndTurn() {
        this.scoreCounter = 1;
        if (Settings.isDebug) {
            this.notebook.outItem();
        }
    }

    @Override
    public void onVictory() {
        this.scoreCounter = 1;
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        // line 286 in GameActionManager
        if (card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS) {
            return;
        }
        triggerQuiz();
    }

    public abstract void triggerQuiz();

    @Override
    public void onRightClick() {
        String target = this.notebook.rndGetId();
        if (target.isEmpty()) {
            this.addToBot(new TalkAction(true, "没有错题", 1.0F, 2.0F));
            return;
        }
        this.addToBot(new CorrectAction(target));
    }

}
