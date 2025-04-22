package CET46InSpire.helpers;

import CET46InSpire.CET46Initializer;
import CET46InSpire.actions.QuizAction;
import CET46InSpire.events.CallOfCETEvent;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.relics.BuildQuizDataRequest;
import CET46InSpire.relics.QuizRelic;
import CET46InSpire.screens.QuizScreen;
import basemod.BaseMod;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class QuizCommand extends ConsoleCommand {
    private static final Logger logger = LogManager.getLogger(QuizCommand.class.getName());

    /**
     * tokens: quiz [relic] [lexicon] [id]
     * relic: str, optional; name in BookEnum or rnd (default)
     * lexicon: str, optional; name in LexiconEnum or rnd (default)
     * id: (str)int, optional; quiz id or rnd (default).
     *                         should be within range, and MUST be rnd if relic or lexicon is rnd
     */
    public QuizCommand() {
        this.minExtraTokens = 0;
        this.maxExtraTokens = 3;
        this.requiresPlayer = true;
        this.simpleCheck = false;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        BookEnum b = null;
        LexiconEnum l = null;
        int id = 0;

        if (tokens.length == 1) {
            tokens = new String[]{"quiz", "rnd"};
        }
        if (tokens.length > 1) {
            if (tokens[1].equalsIgnoreCase("--help")) {
                helpMsg();
                return;
            }
            if (tokens[1].equalsIgnoreCase("rnd")) {
                // rnd quiz, relic & lexicon & id
                b = BookEnum.values()[MathUtils.random(BookEnum.values().length - 1)];
                l = CET46Initializer.allBooks.get(b).lexicons.get(
                        MathUtils.random(CET46Initializer.allBooks.get(b).lexicons.size() - 1));
                id = MathUtils.random(BookConfig.VOCABULARY_MAP.get(l) - 1);
            } else {
                b = CallOfCETEvent.getBook(tokens[1]);
                if (b == null) {
                    buildErrorMsg("could not parse relic: " + tokens[1] + ".");
                    return;
                }
            }
        }
        if (tokens.length > 2) {
            if (tokens[1].equalsIgnoreCase("rnd") && !tokens[2].equalsIgnoreCase("rnd")) {
                // 不允许在 bookEnum 指名 rnd 的情况下指名非 rnd 的 lexiconEnum
                buildErrorMsg("lexicon must be rnd when relic is rnd.");
                return;
            }
            if (tokens[2].equalsIgnoreCase("rnd")) {
                // rnd quiz, lexicon & id (if not given)
                if (l == null) {
                    l = CET46Initializer.allBooks.get(b).lexicons.get(
                            MathUtils.random(CET46Initializer.allBooks.get(b).lexicons.size() - 1));
                    id = MathUtils.random(BookConfig.VOCABULARY_MAP.get(l) - 1);
                }
            } else {
                l = BookConfig.getLexicon(tokens[2]);
                if (l == null) {
                    buildErrorMsg("could not parse lexicon: " + tokens[2] + ".");
                    return;
                }
                // 检查使用的 lexicon 是否合法
                if (!CET46Initializer.allBooks.get(b).lexicons.contains(l)) {
                    buildErrorMsg("lexicon is not valid: " + tokens[2] + " not in " + tokens[1] + ".");
                    return;
                }
                if (tokens.length == 3) {
                    id = MathUtils.random(BookConfig.VOCABULARY_MAP.get(l) - 1);
                }
            }
        }
        if (tokens.length == 4) {
            if (tokens[1].equalsIgnoreCase("rnd") || tokens[2].equalsIgnoreCase("rnd")) {
                // 不允许在 bookEnum 或 lexiconEnum 指名 rnd 的情况下使用第三个额外 token
                buildErrorMsg("random relic or lexicon could never use 4th token.");
                return;
            }
            if (!NumberUtils.isDigits(tokens[3])) {
                buildErrorMsg("word id must be an integer: " + tokens[3] + ".");
                return;
            }
            id = Integer.parseInt(tokens[3]);
            // 这个时候必然有非 null 的 l
            if (id < 0 || id >= BookConfig.VOCABULARY_MAP.get(l)) {
                buildErrorMsg("word id out of range: " + tokens[3] + ".");
                return;
            }

        }
        logger.info("Successfully parse: BookEnum: {}, LexiconEnum: {}, ID: {}", b, l, id);
        executeQuiz(b, l, id);
    }

    private void executeQuiz(BookEnum book, LexiconEnum lexicon, int id) {
        AbstractRelic relic = RelicLibrary.getRelic(QuizRelic.toId(book));
        if (! (relic instanceof QuizRelic)) {
            logger.error("Unknown Error: relic is not QuizRelic: {}", relic);
            return;
        }
        QuizAction.QuizData quizData = ((QuizRelic) relic).buildQuizData(BuildQuizDataRequest.Factory.fromTargetId(lexicon, id));
        logger.info("quizData = {}", quizData);
        // TODO 有可能这里不应该把 quizData 的相关字段改成 public
        BaseMod.openCustomScreen(QuizScreen.Enum.WORD_SCREEN, quizData.show, lexicon.name(),
                quizData.correctOptions, quizData.allOptions, quizData.getWordUiStringsId(), false);
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        // 需要注意的是,如果用tap的话tokens会形成[xx, xx, ]的结构,就是说length会比预计多1
        ArrayList<String> result = new ArrayList<>();
        switch (tokens.length) {
            case 2: {
                result.add("--help");
                result.add("rnd");
                for (BookEnum b: BookEnum.values()) {
                    result.add(b.name().toLowerCase());
                }
                return result;
            }
            case 3: {
                if (tokens[1].equalsIgnoreCase("rnd") || tokens[1].equalsIgnoreCase("--help")) {
                    complete = true;
                    return result;
                }
                BookEnum tmp = CallOfCETEvent.getBook(tokens[1]);
                if (tmp == null) {
                    return result;
                }
                result.add("rnd");
                for (LexiconEnum l: CET46Initializer.allBooks.get(tmp).lexicons) {
                    result.add(l.name().toLowerCase());
                }
                return result;
            }
            case 4: {
                if (tokens[2].equalsIgnoreCase("rnd")) {
                    complete = true;
                    return result;
                }
                LexiconEnum tmp = BookConfig.getLexicon(tokens[2]);
                if (tmp == null) {
                    return result;
                }
                if (tokens[3].isEmpty()) {
                    result.add("[0," + BookConfig.VOCABULARY_MAP.get(tmp) + ")");
                }
                // 检查范围
                if (NumberUtils.isDigits(tokens[3]) && Integer.parseInt(tokens[3]) < BookConfig.VOCABULARY_MAP.get(tmp)) {
                    result.add(tokens[3]);
                }
                return result;
            }
        }
        return result;
    }

    @Override
    public void errorMsg() {

    }

    private void buildErrorMsg(String err) {
        DevConsole.log(err);
        DevConsole.log("type: quiz --help for more information.");
    }

    private void helpMsg() {
        // 艹了为什么STS的dev不是等宽字体,哪个玩意选的字体
        DevConsole.log("quiz [relic] [lexicon] [id]");
        DevConsole.log("* relic  : name of book    / rnd (default).");
        DevConsole.log("* lexicon: name of lexicon / rnd (default).");
        DevConsole.log("* id     : number of word within range.");
        DevConsole.log("           NOTE: send random word when not given.");
    }

}
