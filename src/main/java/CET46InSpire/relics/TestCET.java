package CET46InSpire.relics;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.Cet46QuizAction;
import CET46InSpire.events.CallOfCETEvent;
import CET46InSpire.helpers.BookConfig;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class TestCET extends QuizRelic {
    public TestCET() {
        super(CallOfCETEvent.BookEnum.CET);
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
    public void triggerQuiz() {
        flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        BookConfig bookConfig = CET46Initializer.allBooks.get(book);
        // TODO 从所有lexicons根据权重选其一
        BookConfig.LexiconEnum usingLexicon = bookConfig.lexicons.get(0);
        this.addToTop(new Cet46QuizAction(bookConfig, usingLexicon));
    }

}
