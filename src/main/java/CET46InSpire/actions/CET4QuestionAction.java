package CET46InSpire.actions;

import basemod.BaseMod;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import CET46InSpire.helpers.ArrayListHelper;
import CET46InSpire.relics.CETRelic;
import CET46InSpire.screens.QuizScreen;

import java.util.ArrayList;

public class CET4QuestionAction extends AbstractGameAction {
    private static final String VOCABULARY_ID = "CET46:CET4_";
    private static final int VOCABULARY_SIZE;
    private static final int MAX_ANS_NUM;
    private static final int MAX_MEANING_NUM;

    public CET4QuestionAction() {
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER) {
            int word_id = MathUtils.random(0, VOCABULARY_SIZE - 1);
            UIStrings tmp = CardCrawlGame.languagePack.getUIString(VOCABULARY_ID + word_id);
            String word = null;
            ArrayList<String> right_ans_list = new ArrayList<>();
            for (String item: tmp.TEXT) {
                if (word == null) {
                    word = item;
                    continue;
                }
                right_ans_list.add(item);
            }
            right_ans_list = ArrayListHelper.choose(right_ans_list, MAX_ANS_NUM);

            ArrayList<String> meaning_list = new ArrayList<>();
            // copy
            for (String item: right_ans_list) {
                meaning_list.add(new String(item));
            }
            int choice_num = 3 * right_ans_list.size();
            if (choice_num > MAX_MEANING_NUM) {
                choice_num = MAX_MEANING_NUM;
            }
            for (int i = meaning_list.size(); i < choice_num;) {
                int target_word = MathUtils.random(0, VOCABULARY_SIZE - 1);
                if (target_word == word_id) {
                    continue;
                }
                tmp = CardCrawlGame.languagePack.getUIString(VOCABULARY_ID + target_word);
                int target_meaning = MathUtils.random(1, tmp.TEXT.length - 1);
                meaning_list.add(tmp.TEXT[target_meaning]);
                i++;
            }
            meaning_list = ArrayListHelper.shuffle(meaning_list);

            BaseMod.openCustomScreen(QuizScreen.Enum.WORD_SCREEN, word, right_ans_list, meaning_list);
            tickDuration();
            return;
        }
        tickDuration();

    }

    static {
        VOCABULARY_SIZE = CETRelic.VOCABULARY_CET4;
        MAX_ANS_NUM = 3;
        MAX_MEANING_NUM = 9;
    }
}
