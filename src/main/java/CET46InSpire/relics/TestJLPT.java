package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.Cet46QuizAction;
import CET46InSpire.actions.JlptQuizAction;
import CET46InSpire.actions.QuizAction;
import CET46InSpire.events.CallOfCETEvent;
import CET46InSpire.helpers.BookConfig;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class TestJLPT extends QuizRelic {
    public TestJLPT() {
        super(CallOfCETEvent.BookEnum.JLPT);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TestJLPT();
    }

    @Override
    public String updateDesByLexicon(BookConfig.LexiconEnum lexiconEnum) {
        if (lexiconEnum == null) {
            return "NULL";
        }
        switch (lexiconEnum) {
            case N1:
                return DESCRIPTIONS[0];
            case N2:
                return DESCRIPTIONS[1];
            case N3:
                return DESCRIPTIONS[2];
            case N4:
                return DESCRIPTIONS[3];
            case N5:
                return DESCRIPTIONS[4];
        }
        return "???";
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        // line 286 in GameActionManager
        if (card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS) {
            return;
        }
        triggerQuiz();
    }


}
