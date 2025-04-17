package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.Cet46QuizAction;
import CET46InSpire.actions.CorrectAction;
import CET46InSpire.actions.JlptQuizAction;
import CET46InSpire.actions.QuizAction;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.helpers.ImageElements;
import CET46InSpire.savedata.CorrectionNote;
import CET46InSpire.ui.ModConfigPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class QuizRelic extends AbstractRelic implements ClickableRelic {
    private static final Logger logger = LogManager.getLogger(QuizRelic.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CET46:RelicUI");
    public int preScoreCounter;
    public int scoreCounter;
    private AbstractCard currentCard;
    protected final BookEnum book;
    public CorrectionNote notebook;
    private boolean isGivenPotion;
    public QuizRelic(BookEnum b, RelicTier tier, LandingSound sfx) {
        super(toId(b), "", tier, sfx);
        this.book = b;
        this.counter = 0;   // perfect counter
        //this.scoreCounter = 1;
        this.preScoreCounter = this.scoreCounter;
        this.notebook = new CorrectionNote();
        this.isGivenPotion = false;
        // init的时候是在receiveEditRelics, 这个时候似乎会找不到数据; 不过这个地方是为了防止maeCopy的时候数据没有更新.
        this.resetTexture();
        this.resetDescription();
    }

    public QuizRelic(BookEnum b) {
        this(b, RelicTier.SPECIAL, LandingSound.CLINK);
    }


    public static String toId(BookEnum b) {
        return CET46Initializer.JSON_MOD_KEY + b.name() + "_relic";
    }

    /**
     * 在打出牌之前进行测验, 但目前不清楚是否需要检查是否已经进行测验
     */
    public void sendQuizPrePlay(AbstractCard currentCard) {
        logger.info("There should be a quiz, but now u get a 0!");
        this.scoreCounter = 0;
        this.currentCard = currentCard;
    }

    /**
     * 答题后进行数据修正
     */
    public void changeCardAfterQuiz(int newScore, boolean isPerfect) {
        logger.info("changeCardAfterQuiz newScore = {}", newScore);
        this.scoreCounter = newScore;
        if (isPerfect) {
            this.counter++;
        } else {
            this.counter = 0;
        }

        currentCard.damage *= currentCard.damage > 0 ? this.scoreCounter : 1;
        currentCard.block *= currentCard.block > 0 ? this.scoreCounter : 1;
        currentCard.magicNumber *= currentCard.magicNumber > 0 ? this.scoreCounter : 1;
        logger.info("Change Card: {}: D: {}, B: {}, M: {}", currentCard, currentCard.damage, currentCard.block, currentCard.magicNumber);
    }

    public void resetTexture() {
        this.img = ImageElements.getLexiconTexture(ModConfigPanel.getWeightedLexicon(this.book));
        this.img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.outlineImg = ImageElements.RELIC_CET_OUTLINE;
        this.outlineImg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void resetDescription() {
        this.description = updateDesByLexicon(ModConfigPanel.getWeightedLexicon(this.book));
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    public String updateDesByLexicon(LexiconEnum lexiconEnum) {
        return "";
    }

    @Override
    public void update() {
        super.update();
        if (this.preScoreCounter != this.scoreCounter) {
            this.preScoreCounter = this.scoreCounter;
            // TODO 是否改用patch，不需要此处了？
            // AbstractDungeon.player.getPower(ChangePowersApplyPower.POWER_ID).updateDescription();
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
        // TODO 是否改用patch，不需要此处了？
        // this.addToTop(new ApplyPowerAction(AbstractDungeon.player, null,
        //         new ChangePowersApplyPower(AbstractDungeon.player, this)));
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
        //this.scoreCounter = 1;
        if (Settings.isDebug) {
            this.notebook.outItem();
        }
    }

    @Override
    public void onVictory() {
        //this.scoreCounter = 1;
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
        BookConfig bookConfig = CET46Initializer.allBooks.get(book);
        // TODO 从所有lexicons根据权重选其一
        BookConfig.LexiconEnum usingLexicon = bookConfig.lexicons.get(0);
        QuizAction quizAction;
        switch (usingLexicon) {
            case CET4:
            case CET6:
                quizAction = new Cet46QuizAction(bookConfig, usingLexicon);
                break;
            default:
                quizAction = new JlptQuizAction(bookConfig, usingLexicon);
        }
        this.addToTop(quizAction);
    };

    @Override
    public void onRightClick() {
        // 这个只应该在战斗房间触发
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT ||
                AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE) {
            return;
        }
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
