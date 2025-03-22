package CET46InSpire.relics;

import basemod.AutoAdd;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AutoAdd.Ignore
public class CETRelic extends CustomRelic {
    private static final Logger logger = LogManager.getLogger(CETRelic.class.getName());
    public static final int VOCABULARY_CET4;


    public CETRelic(String id, Texture texture, Texture outline, RelicTier tier, LandingSound sfx) {
        super(id, texture, outline, tier, sfx);
        this.counter = 1;   // score
    }

    public void getScore() {
        if (Settings.isDebug) {
            logger.info("Get Score");
        }
        this.counter = 1;
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
    }
}
