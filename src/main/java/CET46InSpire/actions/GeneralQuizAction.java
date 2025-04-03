package CET46InSpire.actions;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.CET46Settings;
import CET46InSpire.ui.CET46Panel.BookConfig;

public class GeneralQuizAction extends QuizAction {

    public GeneralQuizAction(BookConfig bookConfig) {
        super(
                bookConfig.bookEnum.name(),
                CET46Initializer.JSON_MOD_KEY + bookConfig.bookEnum.name() + "_",
                CET46Settings.VOCABULARY_MAP.get(bookConfig.bookEnum)
        );
    }
}
