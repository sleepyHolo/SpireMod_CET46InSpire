package CET46InSpire.actions;

import CET46InSpire.helpers.ArrayListHelper;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.screens.QuizScreen;
import CET46InSpire.ui.CET46Panel;
import basemod.BaseMod;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class CorrectAction extends AbstractGameAction {
    private static final Logger logger = LogManager.getLogger(CorrectAction.class.getName());
    private static final String LEXICON;
    private final String target;
    private final int target_id;
    private final String lexicon;
    private final int size;

    public CorrectAction(String target) {
        this.target = target;
        String[] tmp = this.target.split("_");
        this.target_id = Integer.parseInt(tmp[1]);
        this.lexicon = tmp[0] + "_";
        this.size = BookConfig.getLexiconSize(this.lexicon);
        logger.info("lexicon: {}, size: {}", lexicon, size);
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER) {
            UIStrings tmp = CardCrawlGame.languagePack.getUIString(this.target);
            String word = null;
            ArrayList<String> right_ans_list = new ArrayList<>();
            for (String item: tmp.TEXT) {
                if (word == null) {
                    word = item;
                    continue;
                }
                right_ans_list.add(item);
            }
            right_ans_list = ArrayListHelper.choose(right_ans_list, CET46Panel.maxAnsNum);

            ArrayList<String> meaning_list = new ArrayList<>();
            // copy
            for (String item: right_ans_list) {
                meaning_list.add(new String(item));
            }
            int choice_num = 3 * right_ans_list.size();
            if (choice_num > QuizAction.MAX_MEANING_NUM) {
                choice_num = QuizAction.MAX_MEANING_NUM;
            }
            for (int i = meaning_list.size(); i < choice_num;) {
                int target_word = MathUtils.random(0, this.size - 1);
                if (target_word == this.target_id) {
                    continue;
                }
                tmp = CardCrawlGame.languagePack.getUIString(this.lexicon + target_word);
                int target_meaning = MathUtils.random(1, tmp.TEXT.length - 1);
                meaning_list.add(tmp.TEXT[target_meaning]);
                i++;
            }
            meaning_list = ArrayListHelper.shuffle(meaning_list);

            BaseMod.openCustomScreen(QuizScreen.Enum.WORD_SCREEN, word, LEXICON,
                    right_ans_list, meaning_list, target, true);

            tickDuration();
            return;
        }
        tickDuration();

    }

    static {
        LEXICON = CardCrawlGame.languagePack.getUIString("CET46:WordScreen").TEXT[4];
    }

}
