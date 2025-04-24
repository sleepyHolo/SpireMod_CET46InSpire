package CET46InSpire.relics;

import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.events.CallOfCETEvent;
import CET46InSpire.helpers.ArrayListHelper;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.ui.ModConfigPanel;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.Collections;

public class TestCET extends QuizRelic {
    public TestCET() {
        super(
                CallOfCETEvent.BookEnum.CET
        );
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TestCET();
    }

    @Override
    public String updateDesByLexicon(BookConfig.LexiconEnum lexiconEnum) {
        if (lexiconEnum == null) {
            return "NULL";
        }
        switch (lexiconEnum) {
            case CET4:
                return DESCRIPTIONS[0];
            case CET6:
                return DESCRIPTIONS[1];
        }
        return "???";
    }

    @Override
    public QuizData buildQuizData(BuildQuizDataRequest request) {
        UIStrings tmp = CardCrawlGame.languagePack.getUIString(request.getTargetUiStringsId());
        String word = null;
        ArrayList<String> right_ans_list = new ArrayList<>();
        for (String item: tmp.TEXT) {
            if (word == null) {
                word = item;
                continue;
            }
            right_ans_list.add(item);
        }
        right_ans_list = ArrayListHelper.choose(right_ans_list, ModConfigPanel.maxAnsNum);

        ArrayList<String> meaning_list = new ArrayList<>();
        // copy
        meaning_list.addAll(right_ans_list);
        int choice_num = 3 * right_ans_list.size();
        if (choice_num > request.getMaxOptionNum()) {
            choice_num = request.getMaxOptionNum();
        }
        for (int i = meaning_list.size(); i < choice_num;) {
            int target_word = MathUtils.random(0, request.getVocabularySize()- 1);
            if (target_word == request.targetId) {
                continue;
            }
            tmp = CardCrawlGame.languagePack.getUIString(request.getUiStringsIdStart() + target_word);
            int target_meaning = MathUtils.random(1, tmp.TEXT.length - 1);
            meaning_list.add(tmp.TEXT[target_meaning]);
            i++;
        }
        Collections.shuffle(meaning_list);
        return new QuizData(request.getTargetId(), request.getTargetUiStringsId(), word, right_ans_list, meaning_list);
    }

}
