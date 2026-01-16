package CET46InSpire.relics;

import CET46InSpire.actions.QuizAction.QuizData;
import CET46InSpire.events.CallOfCETEvent;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BuildQuizDataRequest;
import CET46InSpire.helpers.IQuizDataStrategy.JlptQuizDataStrategy;
import CET46InSpire.helpers.JlptQuizType;
import CET46InSpire.helpers.UIStringGetter;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.*;

public class JLPTRelic extends QuizRelic {

    List<JlptQuizType> activeTypes = Arrays.asList(
            JlptQuizType.ASK_KANA,
            JlptQuizType.ASK_MEANING
    );

    public JLPTRelic() {
        super(CallOfCETEvent.BookEnum.JLPT);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new JLPTRelic();
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
        }
        return "???";
    }

    private static final UIStringGetter CardCrawlGameUIStringGetter = id -> CardCrawlGame.languagePack.getUIString(id);

    /**
     * 单元测试时，使用别的实现方式，避免CardCrawlGame方式频繁读取过慢。
     */
    private static final JlptQuizDataStrategy quizDataStrategy = new JlptQuizDataStrategy(CardCrawlGameUIStringGetter);
    @Override
    public QuizData buildQuizData(BuildQuizDataRequest request) {
        return quizDataStrategy.buildQuizData(request);
    }

}
