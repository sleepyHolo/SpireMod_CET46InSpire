package CET46InSpire.actions;

import CET46InSpire.CET46Initializer;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.relics.QuizRelic;

public class GeneralQuizAction extends QuizAction {

    public GeneralQuizAction(QuizRelic quizRelic, BookConfig bookConfig, LexiconEnum usingLexicon) {
        super(
                quizRelic,
                usingLexicon
        );
    }

}
