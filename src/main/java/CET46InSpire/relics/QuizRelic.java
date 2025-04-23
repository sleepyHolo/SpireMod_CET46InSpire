package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.GeneralQuizAction;
import CET46InSpire.actions.CorrectAction;
import CET46InSpire.actions.QuizAction;
import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.ArrayListHelper;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.helpers.ImageElements;
import CET46InSpire.patches.AbstractPlayerPatch;
import CET46InSpire.powers.PerfectAnsPower;
import CET46InSpire.savedata.CorrectionNote;
import CET46InSpire.ui.ModConfigPanel;
import basemod.TopPanelGroup;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.TheBomb;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.TheBombPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class QuizRelic extends AbstractRelic implements ClickableRelic {
    protected static final Logger logger = LogManager.getLogger(QuizRelic.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CET46:RelicUI");
    public int preScoreCounter;
    public int scoreCounter;
    public boolean quizzed = false;
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
        if (currentCard.type == AbstractCard.CardType.CURSE || currentCard.type == AbstractCard.CardType.STATUS) {
            return;
        }
        this.triggerQuiz();
        this.quizzed = true;
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
        if (AbstractPlayerPatch.c == null) {
            logger.info("No card data. Quiz from console?");
            return;
        }
        AbstractPlayerPatch.p.useCard(AbstractPlayerPatch.c, AbstractPlayerPatch.m, AbstractPlayerPatch.energy);
        AbstractPlayerPatch.c = null;
        if (AbstractDungeon.player.hasPower(PerfectAnsPower.POWER_ID)) {
            AbstractDungeon.player.getPower(PerfectAnsPower.POWER_ID).updateDescription();
        }

    }

    /**
     * 更新卡牌,同时更新currentCard
     * 如果用之前的记录 currentCard 的方法, 在这里给 card 赋值就不行, but why?
     */
    public void changeCardPrePlay(AbstractCard card) {
        if (!this.quizzed) {
            logger.error("Not quizzed!! But why?");
            return;
        }
        card.damage *= card.damage > 0 ? this.scoreCounter : 1;
        card.block *= card.block > 0 ? this.scoreCounter : 1;
        card.magicNumber *= card.magicNumber > 0 ? this.scoreCounter : 1;
        // fix: 炸弹的逻辑问题
        if (card instanceof TheBomb) {
            card.magicNumber = this.scoreCounter == 0 ? 0 : card.baseMagicNumber;
            for (int i = 1; i < this.scoreCounter; i++) {
                this.addToBot(new ApplyPowerAction(AbstractDungeon.player, null,
                        new TheBombPower(AbstractDungeon.player, 3, card.magicNumber)));
            }
        }
        logger.info("Change Card: {}: D: {}, B: {}, M: {}", card, card.damage, card.block, card.magicNumber);
        this.quizzed = false;
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
        // 不再需要更新 PerfectAnsPower 因为那个只和 PerfectCounter 有关;
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
        this.addToTop(new ApplyPowerAction(AbstractDungeon.player, null, new PerfectAnsPower(AbstractDungeon.player, this)));
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

    public void triggerQuiz() {
        flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        BookConfig bookConfig = CET46Initializer.allBooks.get(book);
        BookConfig.LexiconEnum usingLexicon = bookConfig.lexicons.get(0);
        List<LexiconEnum> list = ModConfigPanel.getRelicLexicons(book);
        if (!list.isEmpty()) {
            usingLexicon = list.get(MathUtils.random(list.size() - 1));
        } else {
            logger.error("Relic Lexicon is EMPTY: {}!", book.name());
        }
        QuizAction quizAction = new GeneralQuizAction(this, bookConfig, usingLexicon);
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
        this.addToTop(new CorrectAction(target, this));
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

    /**
     * 根据request及Relic策略，构造QuizData
     */
    public abstract QuizData buildQuizData(BuildQuizDataRequest request);
}
