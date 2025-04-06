package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.CorrectAction;
import CET46InSpire.actions.GeneralQuizAction;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.powers.ChangePowersApplyPower;
import CET46InSpire.savedata.CorrectionNote;
import CET46InSpire.ui.CET46Panel;
import CET46InSpire.helpers.BookConfig;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class CETRelic extends CustomRelic implements ClickableRelic {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CET46:RelicUI");
    public int pre_counter;
    public int scoreCounter = -1;
    public CorrectionNote notebook = null;
    private boolean isGivenPotion = false;
    public BookEnum bookEnum;

    public static String toId(BookEnum bookEnum) {
        return CET46Initializer.JSON_MOD_KEY + bookEnum.name() + "_relic";
    }
    public CETRelic(BookEnum bookEnum, Texture texture, Texture outline, RelicTier tier, LandingSound sfx) {
        super(toId(bookEnum), texture, outline, tier, sfx);
        this.bookEnum = bookEnum;
        this.counter = 0;   // perfect counter
        this.scoreCounter = 1;
        this.pre_counter = this.scoreCounter;
        this.notebook = new CorrectionNote();
        this.isGivenPotion = false;
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
        this.isGivenPotion = false;
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        // line 286 in GameActionManager
        if (card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS) {
            return;
        }
        triggerQuiz();
    }

    public void triggerQuiz() {
        flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        BookConfig bookConfig = CET46Initializer.allBooks.get(bookEnum);
        if (MathUtils.random(0, 99) < CET46Panel.band4RateIn6 || bookConfig.lowerLevelBooks.isEmpty()) {
            this.addToTop(new GeneralQuizAction(bookConfig));
        } else {
            // TODO 从所有lowerLevelBooks中使用某种策略选其一
            this.addToTop(new GeneralQuizAction(CET46Initializer.allBooks.get(bookConfig.lowerLevelBooks.get(0))));
        }
    };

    @Override
    public void onRightClick() {
        String target = this.notebook.rndGetId();
        if (target.isEmpty()) {
            this.givePotion(uiStrings.TEXT[0]);
            return;
        }
        this.addToTop(new CorrectAction(target));
    }

    public void givePotion(String talk) {
        String talkStr = talk.isEmpty() ? talk : talk + " NL ";
        if (this.isGivenPotion) {
            talkStr += uiStrings.TEXT[4];
            this.addToTop(new TalkAction(true, talkStr, 1.0F, 2.0F));
            return;
        }
        if (AbstractDungeon.player.hasRelic("Sozu")) {
            AbstractDungeon.player.getRelic("Sozu").flash();
            this.isGivenPotion = true;
            talkStr += uiStrings.TEXT[2];
            this.addToTop(new TalkAction(true, talkStr, 1.0F, 2.0F));
            return;
        }
        this.isGivenPotion = AbstractDungeon.player.obtainPotion(AbstractDungeon.returnRandomPotion(true));
        if (this.isGivenPotion) {
            talkStr += uiStrings.TEXT[1];
        } else {
            talkStr += uiStrings.TEXT[3];
        }
        this.addToTop(new TalkAction(true, talkStr, 1.0F, 2.0F));
    }

    public void givePotion() {
        this.givePotion("");
    }

}
