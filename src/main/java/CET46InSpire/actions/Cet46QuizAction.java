package CET46InSpire.actions;

import CET46InSpire.CET46Initializer;
import CET46InSpire.helpers.ArrayListHelper;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.ui.CET46Panel;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

public class Cet46QuizAction extends QuizAction {

    public Cet46QuizAction(BookConfig bookConfig) {
        super(
                bookConfig.bookEnum.name(),
                CET46Initializer.JSON_MOD_KEY + bookConfig.bookEnum.name() + "_",
                BookConfig.VOCABULARY_MAP.get(bookConfig.bookEnum)
        );
    }


    @Override
    protected QuizData nextQuiz() {
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
        right_ans_list = ArrayListHelper.choose(right_ans_list, CET46Panel.maxAnsNum);

        ArrayList<String> meaning_list = new ArrayList<>();
        // copy
        meaning_list.addAll(right_ans_list);
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
        return new QuizData(word_id, word, right_ans_list, meaning_list);
    }
}
