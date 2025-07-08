package CET46InSpire.events;

import CET46InSpire.CET46Initializer;
import CET46InSpire.helpers.BookConfig;
import CET46InSpire.relics.QuizRelic;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;
import CET46InSpire.helpers.DayBeforeCETPlayGameException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CallOfCETEvent extends AbstractImageEvent {
    private static final Logger logger = LogManager.getLogger(CallOfCETEvent.class.getName());
    public static final String ID = "CET46:CallOfCETEvent";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static String INTRO_BODY;
    private static String ACCEPT_BODY;
    private static String REFUSE_BODY;
    private static final String OPTION_REFUSE;
    private static final String OPTION_EXIT;
    private static final String OPTION_STUDY;
    private ArrayList<BookEnum> validBooks;
    private EventState state;
    private boolean tomorrowCET;

    public CallOfCETEvent() {
        super(eventStrings.NAME, "test", "CET46Resource/image/events/call_of_cet.png");
        this.tomorrowCET = false;
        this.initBodies();
        this.body = INTRO_BODY;
        state = EventState.INTRO;

        this.initBook();
        if (this.tomorrowCET) {
            this.imageEventText.setDialogOption(OPTION_STUDY);
        }
        this.imageEventText.setDialogOption(OPTION_REFUSE);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (state) {
            case INTRO: {
                if (buttonPressed == this.imageEventText.optionList.size() - 1) {
                    // refuse
                    this.state = EventState.REFUSE;
                    this.imageEventText.updateBodyText(REFUSE_BODY);
                    this.imageEventText.updateDialogOption(0, OPTION_EXIT);
                    if (this.tomorrowCET) {
                        // 4 * study
                        for (int i = 1; i < 4; i++) {
                            if (this.imageEventText.optionList.size() > i) {
                                this.imageEventText.updateDialogOption(i, OPTION_STUDY);
                            } else {
                                this.imageEventText.setDialogOption(OPTION_STUDY);
                            }
                        }
                        this.clearOptions(4);
                    } else {
                        this.clearOptions();
                    }
                    return;
                } else if (this.tomorrowCET && buttonPressed == this.imageEventText.optionList.size() - 2) {
                    // study
                    gotoStudyNow();
                    return;
                }
                // accept
                this.state = EventState.ACCEPT;
                AbstractRelic relicMetric = getBookRelic(this.validBooks.get(buttonPressed));
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(0.5F * Settings.WIDTH, 0.5F * Settings.HEIGHT, relicMetric);
                this.imageEventText.updateBodyText(ACCEPT_BODY);
                this.imageEventText.updateDialogOption(0, OPTION_EXIT);
                if (this.tomorrowCET) {
                    this.imageEventText.updateDialogOption(1, OPTION_STUDY);
                    this.clearOptions(2);
                } else {
                    this.clearOptions();
                }
                return;
            }
            case ACCEPT:
            case REFUSE:
                if ((!this.tomorrowCET) || buttonPressed == 0) {
                    openMap();
                    return;
                }
                // study
                gotoStudyNow();
                return;

        }
        openMap();
    }

    private void initBodies() {
        // 使用2025年的考试时间作参考, 可以自动调整时间预测而不必每半年更新
        // 2025考试时间: 上半年 2025/6/14, 下半年 2025/12/13
        LocalDateTime today = LocalDateTime.now();
        int year = today.getYear();
        LocalDateTime next = LocalDateTime.of(year, 6, 14, 0, 0, 0);
        if (today.isAfter(next)) {
            next = LocalDateTime.of(year, 12, 13, 0, 0, 0);
        }
        if (today.isAfter(next)) {
            next = LocalDateTime.of(year + 1, 6, 14, 0, 0, 0);
        }

        Duration duration = Duration.between(today, next);
        long duration_ = duration.toDays();
        if (duration_ < 1) {
            duration_ = 1;
        }
        String duration_data = String.valueOf(duration_);
        if (duration_ == 1) {
            this.tomorrowCET = true;
        }

        if (this.tomorrowCET) {
            duration_data = eventStrings.DESCRIPTIONS[6];
        } else {
            duration_data = duration_data + eventStrings.DESCRIPTIONS[5];
        }
        INTRO_BODY = eventStrings.DESCRIPTIONS[0] + duration_data + eventStrings.DESCRIPTIONS[1];
        REFUSE_BODY = eventStrings.DESCRIPTIONS[3];
        ACCEPT_BODY = eventStrings.DESCRIPTIONS[4];
        if (this.tomorrowCET) {
            INTRO_BODY += eventStrings.DESCRIPTIONS[7];
            REFUSE_BODY += eventStrings.DESCRIPTIONS[8];
            ACCEPT_BODY += eventStrings.DESCRIPTIONS[9];
        }
    }

    private void initBook() {
        this.validBooks = new ArrayList<>();
        for (BookEnum b: BookEnum.values()) {
            if (!checkBookAvailable(b)) {
                continue;
            }
            this.imageEventText.setDialogOption(getBookOption(b));
            this.validBooks.add(b);
        }
    }

    public static boolean checkBookAvailable(BookEnum b) {
        return CET46Initializer.userBooks.contains(CET46Initializer.allBooks.get(b));
    }

    public static String getBookOption(BookEnum b) {
        StringBuilder sb = new StringBuilder();
        sb.append(eventStrings.OPTIONS[4]);
        BookConfig config = CET46Initializer.allBooks.get(b);
        if (config == null) {
            logger.info("wtf? what happened?");
            return eventStrings.OPTIONS[3];
        }
        sb.append(config.relicSupplier.get().name);
        sb.append(eventStrings.OPTIONS[5]);
        return sb.toString();
    }

    public static AbstractRelic getBookRelic(BookEnum b) {
        String id = QuizRelic.toId(b);
        return RelicLibrary.getRelic(id).makeCopy();
    }

    private void gotoStudyNow() {
        throw new DayBeforeCETPlayGameException();
    }

    public void clearOptions(int remainNum) {
        if (this.imageEventText.optionList.size() > remainNum) {
            this.imageEventText.optionList.subList(remainNum, this.imageEventText.optionList.size()).clear();
        }

        for (LargeDialogOptionButton b : this.imageEventText.optionList) {
            b.calculateY(this.imageEventText.optionList.size());
        }

    }

    public void clearOptions() {
        this.clearOptions(1);
    }

    static {
        OPTION_EXIT = eventStrings.OPTIONS[0];
        OPTION_REFUSE = eventStrings.OPTIONS[1];
        OPTION_STUDY = eventStrings.OPTIONS[2];
    }

    public enum EventState {
        INTRO,
        ACCEPT,
        REFUSE,
    }

    public enum BookEnum {
        CET,
        JLPT
    }

    @Nullable
    public static BookEnum getBook(String book) {
        for (BookEnum b: BookEnum.values()) {
            if (b.name().equalsIgnoreCase(book)) {
                return b;
            }
        }
        return null;
    }

}