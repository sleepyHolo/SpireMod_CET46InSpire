package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.helpers.ImageElements;
import CET46InSpire.ui.ModConfigPanel;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class QuizRelic extends AbstractRelic {
    private static final Logger logger = LogManager.getLogger(QuizRelic.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CET46:RelicUI");
    public int pre_counter = 0;
    public int scoreCounter = 0;
    private final BookEnum book;
    private List<LexiconEnum> lexicons;

    public QuizRelic(BookEnum b, RelicTier tier, LandingSound sfx) {
        super(toId(b), "", tier, sfx);
        this.book = b;
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
    public void sendQuizPrePlay() {
        logger.info("There should be a quiz, but now u get a 0!");
        this.scoreCounter = 0;
    }

    /**
     * 在牌进入 Manager 队列之前进行数据修正
     */
    public void changeCardPrePlay(AbstractCard c) {
        c.damage *= c.damage > 0 ? this.scoreCounter : 1;
        c.block *= c.block > 0 ? this.scoreCounter : 1;
        c.magicNumber *= c.magicNumber > 0 ? this.scoreCounter : 1;
        logger.info("Change Card: {}: D: {}, B: {}, M: {}", c, c.damage, c.block, c.magicNumber);
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

}
