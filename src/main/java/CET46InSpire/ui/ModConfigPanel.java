package CET46InSpire.ui;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import CET46InSpire.relics.QuizRelic;
import basemod.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static basemod.EasyConfigPanel.ConfigField.FieldSetter;

public class ModConfigPanel extends ModPanel {
    private static final Logger logger = LogManager.getLogger(ModConfigPanel.class.getName());
    /**
     * pages用于标记参数的显示页数, String是 elementData 的 key
     */
    private static final List<List<String>> pages;
    private static final int configPageNum;
    /**
     * intRange 用于记录 int 型数据的滑块范围，key是 pages的字段
     */
    private static final HashMap<String, List<Integer>> intRange;
    /**
     * key: Button的elementId，内部定义；是 load + BooEnum.name
     * value: 点击这个Button影响的List<LexiconEnum>
     */
    private static final HashMap<String, List<LexiconEnum>> lexiconMap;
    private int pageNum = 0;
    /**
     * 所有IUIElement的elementId映射，elementId是内部定义（开发者自行约定，非框架规定）；
     */
    private HashMap<String, IUIElement> elementData;
    private UIStrings uiStrings = null;
    private final SpireConfig config;

    private ModLabel pageTitle;
    private ModLabeledButton pageForward;
    private ModLabeledButton pageBackward;
    private ModLabeledButton pageReturn;

    private static final float PAGE_TITLE_X;
    private static final float PAGE_TITLE_Y;
    private static final float ELEMENT_X;
    private static final float ELEMENT_Y;
    private static final List<Float> PADDINGS_Y;
    private static final float PAGE_BUTTON_X1;
    private static final float PAGE_BUTTON_X2;
    private static final float PAGE_BUTTON_Y;

    private static final float LEXICON_X;
    private static final float LEXICON_Y;
    private static final float LEXICON_PAD_X;
    private static final float LEXICON_PAD_Y;
    private static final float BUTTON_DELTA_X1;
    private static final float BUTTON_DELTA_X2;
    private static final float BUTTON_DELTA_Y;
    private static final float WEIGHT_DELTA_X;
    private static final float WEIGHT_DELTA_Y;

    /**
     * 以下是实际使用的参数
     */
    public static boolean darkMode = false;
    public static boolean pureFont = true;
    public static boolean fastMode = false;
    public static boolean casualMode = false;
    public static boolean ignoreCheck = false;
    public static boolean showLexicon = true;
    public static int maxAnsNum = 3;
    public static boolean loadJLPT = true;
    public static boolean loadCET = true;

    /**
     * 这个是遗物对应的词库权重
     * lexiconData Map<RelicName_LexiconName, Weight>>>
     */
    public static HashMap<String, Integer> lexiconData;
    public static HashMap<BookEnum, LexiconEnum> weightedLexicon;
    public static HashMap<BookEnum, List<LexiconEnum>> relicLexicon;

    static {
        pages = new ArrayList<>();
        pages.add(Arrays.asList("darkMode", "pureFont", "fastMode", "casualMode", "ignoreCheck", "showLexicon", "maxAnsNum"));
        List<String> page2 = new ArrayList<>();     // 第二页不能用Arrays.asList 因为预计将修改其内容
        page2.add("loadCET");
        page2.add("loadJLPT");
        pages.add(page2);

        configPageNum = 2;
        lexiconMap = new HashMap<>();
        lexiconData = new HashMap<>();
        weightedLexicon = new HashMap<>();
        relicLexicon = new HashMap<>();

        // 只读数据 int range
        intRange = new HashMap<>();
        intRange.put("maxAnsNum", Arrays.asList(1, 3));
    }


    public ModConfigPanel() {
        // init config
        try {
            Properties configDefaults = new Properties();
            for (int i = 0; i < configPageNum; i++) {
                List<String> page = pages.get(i);
                for (String name: page) {
                    Field field = this.getClass().getField(name);
                    configDefaults.put(field.getName(), String.valueOf(field.get(null)));
                }
            }
            // 词典数据
            for (Map.Entry<String, Integer> entry: lexiconData.entrySet()) {
                configDefaults.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
            this.config = new SpireConfig(CET46Initializer.MOD_ID, "config", configDefaults);
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            throw new RuntimeException("Failed to set up SpireConfig for " + CET46Initializer.MOD_ID, e);
        }

    }

    /**
     * @return 返回加权后的词库列表,即权重等于元素在列表出现的数量
     */
    public static List<LexiconEnum> getRelicLexicons(BookEnum b) {
        return relicLexicon.getOrDefault(b, new ArrayList<>());
    }

    public static LexiconEnum getWeightedLexicon(BookEnum b) {
        return weightedLexicon.getOrDefault(b, null);
    }

    /**
     * 初始化界面元素, 配置变量读取由类初始化完成; 须保证调用时languagePack初始化已经完成
     */
    public void receivePostInitialize() {
        this.uiStrings = CardCrawlGame.languagePack.getUIString(CET46Initializer.JSON_MOD_KEY + "ConfigPanel");
        this.elementData = new HashMap<>();
        this.initUIElements();
        this.initRelicPages();
        this.setPage(0);
        this.checkReset();
        this.updateWeights();   // 保证更新词库权重
        this.resetAllQuizRelics();      // 从 initializer 搬过来的
    }

    private void initUIElements() {
        pageTitle = new ModLabel("No Title", PAGE_TITLE_X, PAGE_TITLE_Y, this, (text) -> {});
        // 配置属性
        try {
            for (int i = 0; i < configPageNum; i++) {
                List<String> page = pages.get(i);
                float pagePos = ELEMENT_Y;
                for (String name: page) {
                    // 获取属性
                    Field field = this.getClass().getField(name);
                    IUIElement element = buildElement(field, name);
                    if (element != null) {
                        element.setY(pagePos);
                        this.elementData.put(name, element);
                        // 处理整数部分, 这个时候 element 是 slider, 还有个 label 没有设置位置
                        if (elementData.containsKey(name + "_label")) {
                            elementData.get(name + "_label").set(ELEMENT_X + 40.0F, pagePos);
                            pagePos -= PADDINGS_Y.get(i);
                            element.set(ELEMENT_X + 40.0F, pagePos);
                        }
                        // 更新位置
                        pagePos -= PADDINGS_Y.get(i);
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // 翻页按钮
        pageForward = new ModLabeledButton(">", PAGE_BUTTON_X1, PAGE_BUTTON_Y, Settings.CREAM_COLOR, Color.WHITE,
                FontHelper.cardEnergyFont_L, this, (button) -> {this.nextPage(true);});
        pageBackward = new ModLabeledButton("<", PAGE_BUTTON_X2, PAGE_BUTTON_Y, Settings.CREAM_COLOR, Color.WHITE,
                FontHelper.cardEnergyFont_L, this, (button) -> {this.nextPage(false);});
        pageReturn = new ModLabeledButton(uiStrings.EXTRA_TEXT[1], PAGE_BUTTON_X2, PAGE_BUTTON_Y, Settings.CREAM_COLOR,
                Color.WHITE, FontHelper.cardEnergyFont_L, this, (button) -> {
            try {
                if (!this.checkWeights()) {
                    return;
                }
                for (String s: pages.get(this.pageNum)) {
                    if (lexiconData.containsKey(s)) {
                        this.config.setString(s, String.valueOf(lexiconData.get(s)));
                    }
                }
                this.config.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.setPage(1);
        });

    }

    private void initRelicPages() {
        for (Map.Entry<String, List<LexiconEnum>> entry: lexiconMap.entrySet()) {
            List<String> page = new ArrayList<>();
            IUIElement tmp = null;
            IUIElement base = elementData.getOrDefault(entry.getKey(), null);
            if (base == null) {
                continue;
            }
            int target = pages.size();
            tmp = new ModLabeledButton(uiStrings.EXTRA_TEXT[0], Math.max(base.getX() + 200.0F, 1000.0F), base.getY() - 2.0F,
                    Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, this, (button) -> {this.setPage(target);});
            // 目前来说Relic肯定是第二页
            (pages.get(1)).add(entry.getKey() + "_jump");
            elementData.put(entry.getKey() + "_jump", tmp);
            // 设置显示比例的部分
            tmp = new ModLabel("", base.getX() + 25.0F, base.getY() - PADDINGS_Y.get(0), this, (text) -> {});
            (pages.get(1)).add(entry.getKey() + "_display");
            elementData.put(entry.getKey() + "_display", tmp);

            for (int i = 0; i < entry.getValue().size(); i++) {
                LexiconEnum l = entry.getValue().get(i);
                String tmp_name = entry.getKey() + "_" + l.name();
                // load weights
                lexiconData.put(tmp_name, Integer.parseInt(this.config.getString(tmp_name)));

                float x = LEXICON_X + LEXICON_PAD_X * (float) (i % 3);
                float y = LEXICON_Y + LEXICON_PAD_Y * (float) (i / 3);
                // lexicon text
                tmp = new ModLabel(uiStrings.TEXT_DICT.getOrDefault(l.name(), l.name()), x, y, this, (label) -> {});
                page.add(tmp_name + "_name");
                elementData.put(tmp_name + "_name", tmp);
                // lexicon button
                tmp = new ModLabeledButton("+", x + BUTTON_DELTA_X1, y + BUTTON_DELTA_Y,
                        Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, this, (button) -> {
                    lexiconData.put(tmp_name, Math.min(10, lexiconData.get(tmp_name) + 1));
                    ((ModLabel) elementData.get(tmp_name)).text = lexiconData.get(tmp_name).toString();
                });
                page.add(tmp_name + "_add");
                elementData.put(tmp_name + "_add", tmp);
                tmp = new ModLabeledButton("-", x + BUTTON_DELTA_X2, y + BUTTON_DELTA_Y,
                        Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, this, (button) -> {
                    lexiconData.put(tmp_name, Math.max(0, lexiconData.get(tmp_name) - 1));
                    ((ModLabel) elementData.get(tmp_name)).text = lexiconData.get(tmp_name).toString();
                });
                page.add(tmp_name + "_sub");
                elementData.put(tmp_name + "_sub", tmp);
                // weight display
                tmp = new ModLabel(lexiconData.get(tmp_name).toString(), x + WEIGHT_DELTA_X, y + WEIGHT_DELTA_Y, this, (label) -> {});
                page.add(tmp_name);
                elementData.put(tmp_name, tmp);
            }
            pages.add(page);
            logger.info("Successfully initialize page for: {}", entry.getKey());
        }
    }

    public static void addRelicPage(BookEnum relicBook, List<LexiconEnum> list, int default_) {
        relicLexicon.put(relicBook, new ArrayList<>());
        // varName是挂靠的字段名, 需要在elementData里面, 否则没有效果
        String varName = "load" + relicBook.name();
        lexiconMap.put(varName, list);
        for (LexiconEnum l: list) {
            lexiconData.put(varName + "_" + l.name(), default_);
        }
    }

    public static void addRelicPage(BookEnum relicBook, List<LexiconEnum> list) {
        addRelicPage(relicBook, list, 1);
    }

    public void nextPage(boolean forward) {
        if (forward) {
            this.pageNum++;
            if (this.pageNum >= configPageNum) {
                this.pageNum = 0;
            }
        } else {
            this.pageNum--;
            if (this.pageNum < 0) {
                this.pageNum = configPageNum - 1;
            }
        }
        setPage(this.pageNum);
    }

    public void setPage(int id) {
        if (pages.isEmpty()) {
            return;
        }
        if (id < 0) {
            id = pages.size() - 1;
        }
        id %= pages.size();

        this.pageNum = id;
        List<IUIElement> pageElements = new ArrayList<>();
        if (uiStrings != null && id < uiStrings.TEXT.length && !uiStrings.TEXT[id].isEmpty()) {
            this.pageTitle.text = uiStrings.TEXT[id];
            pageElements.add(this.pageTitle);
        }
        for (String name: pages.get(id)) {
            pageElements.add(this.elementData.get(name));
            // 哪个智障设计的, 补丁打到这里来了; 啊 原来我是智障啊
            if (this.elementData.containsKey(name + "_label")) {
                pageElements.add(this.elementData.get(name + "_label"));
            }
        }
        if (this.pageNum < configPageNum) {
            // 翻页按钮
            pageElements.add(this.pageForward);
            pageElements.add(this.pageBackward);
        } else {
            // 返回按钮
            pageElements.add(this.pageReturn);
        }
        this.resetElements(pageElements);
        // update weight display
        if (id == 1) {
            this.updateWeights();
        }
    }

    private IUIElement buildElement(Field field, String name) throws IllegalAccessException {
        if (field.getType() == boolean.class) {
            // load
            field.set(null, Boolean.parseBoolean(this.config.getString(field.getName())));
            return new ModLabeledToggleButton(uiStrings.TEXT_DICT.getOrDefault(name, name), ELEMENT_X, 0.0F,
                    Settings.CREAM_COLOR, FontHelper.charDescFont, (Boolean)field.get(null), this, (label) -> {},
                    (button) -> {saveVar(button.enabled, field, s -> {field.set(null, Boolean.parseBoolean(s));});});
        }
        if (field.getType() == int.class) {
            // load
            field.set(null, Integer.parseInt(this.config.getString(field.getName())));
            // label, 这块处理的有点糟糕了
            elementData.put(name + "_label",
                    new ModLabel(uiStrings.TEXT_DICT.getOrDefault(name, name), ELEMENT_X, 0.0F,
                            Settings.CREAM_COLOR, FontHelper.charDescFont, this, (text) -> {}));
            return new ModMinMaxSlider("", ELEMENT_X, 10.0F, intRange.get(name).get(0), intRange.get(name).get(1),
                    (Integer)field.get(null), "%.0f", this,
                    (slider) -> {saveVar(slider.getValue(), field, s -> {field.set(null, Math.round(Float.parseFloat(s)));});});
        }
        return null;
    }

    private void saveVar(Object var, Field field, FieldSetter setter) {
        try {
            setter.set(var.toString());
            this.config.setString(field.getName(), field.get(null).toString());
            this.config.save();
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用来避免update迭代时修改元素
     */
    private boolean updateStop = false;
    private List<IUIElement> tmpCache = null;
    private void resetElements(List<IUIElement> list) {
        this.tmpCache = list;
        this.updateStop = true;
    }
    private void checkReset() {
        if (!this.updateStop) {
            return;
        }
        this.getUpdateElements().clear();
        this.getRenderElements().clear();
        if (tmpCache != null) {
            for (IUIElement element: tmpCache) {
                this.addUIElement(element);
            }
            tmpCache = null;
        }
        this.updateStop = false;
    }

    @Override
    public void update() {
        for (IUIElement element: this.getUpdateElements()) {
            element.update();
        }
        this.checkReset();

        if (InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            BaseMod.modSettingsUp = false;
        }

        if (!BaseMod.modSettingsUp) {
            this.waitingOnEvent = false;
            Gdx.input.setInputProcessor(this.oldInputProcessor);
            CardCrawlGame.mainMenuScreen.lighten();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.cancelButton.hideInstantly();
            this.isUp = false;
            // 将面板返回首页, 放在前面那个逻辑块就不行, but why?
            this.setPage(0);
            this.checkReset();
            this.resetAllQuizRelics();
        }

    }

    /**
     * 在 RelicLibrary 中更新所有位于 specialList 的 QuizRelic 的图片和描述
     */
    public void resetAllQuizRelics() {
        for (AbstractRelic r: RelicLibrary.specialList) {
            if (r instanceof QuizRelic) {
                ((QuizRelic) r).resetTexture();
                ((QuizRelic) r).resetDescription();
            }
        }
    }

    /**
     * relicLexicon和weightedLexicon被赋值
     */
    private void updateWeights() {
        // 开摆了, 直接一坨循环搞定得了, 自己搓优化不如求大佬重写逻辑
        for (Map.Entry<String, List<LexiconEnum>> entry: lexiconMap.entrySet()) {
            IUIElement element = elementData.getOrDefault(entry.getKey() + "_display", null);
            if (!(element instanceof ModLabel)) {
                continue;
            }
            int total = 0;
            int max = 0;    // 用于记录最大权重, 在下一个循环获取第一次出现的最大lexicon, 这样就不需要使用pair
            List<LexiconEnum> notZeroList = new ArrayList<>();
            for (LexiconEnum l: entry.getValue()) {
                int tmp = lexiconData.getOrDefault(entry.getKey() + "_" + l.name(), 0);
                if (tmp != 0) {
                    notZeroList.add(l);
                    total += tmp;
                }
                if (tmp > max) {
                    max = tmp;
                }
            }
            float k = total == 0 ? 0.0F : 100.0F / total;
            StringBuilder sb = new StringBuilder();
            BookEnum relic = BookEnum.valueOf(entry.getKey().substring(4));     // 去掉前面的 load
            // 更新权重列表
            ArrayList<LexiconEnum> lexicons = (ArrayList<LexiconEnum>) getRelicLexicons(relic);
            lexicons.clear();

            LexiconEnum weighted = null;
            // update text
            for (LexiconEnum l: notZeroList) {
                int tmp = lexiconData.get(entry.getKey() + "_" + l.name());
                if (weighted == null && tmp == max) {
                    weighted = l;
                }
                for (int i = 0; i < tmp; i++) {
                    lexicons.add(l);    // 添加权重个词库
                }
                sb.append(uiStrings.TEXT_DICT.getOrDefault(l.name(), l.name())).append(": ");
                sb.append(Math.round(k * tmp)).append("%. ");
            }
            relicLexicon.put(relic, lexicons);
            ((ModLabel) element).text = sb.toString();
            weightedLexicon.put(relic, weighted);   // 注意 weighted 有可能是 null
            logger.info("Weights updated.");
            logLexicons();

        }
    }

    /**
     * debug, 输出目前所有加权词库列表和使用的最大权词库
     */
    public static void logLexicons() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====== Lexicons Info ======");
        weightedLexicon.forEach((book, weightedL) -> {
            sb.append("\n---- Book-").append(book.name()).append(" (")
                    .append(weightedL == null ? "NULL" : weightedL.name()).append(") ----");
            sb.append("\n> Lexicons: ").append(getRelicLexicons(book).toString());
        });
        sb.append("\n======== Info  End ========");
        logger.info(sb.toString());
    }

    private boolean checkWeights() {
        for (Map.Entry<String, List<LexiconEnum>> entry: lexiconMap.entrySet()) {
            int total = 0;
            for (LexiconEnum l: entry.getValue()) {
                total += lexiconData.getOrDefault(entry.getKey() + "_" + l.name(), 0);
            }
            if (total == 0) {
                this.updateColor(entry, Color.RED);
                logger.error("Weights should not be ZEROS.");
                return false;
            }
            this.updateColor(entry, Color.WHITE);
        }
        return true;
    }

    private void updateColor(Map.Entry<String, List<LexiconEnum>> entry, Color c) {
        for (LexiconEnum l: entry.getValue()) {
            IUIElement e = elementData.getOrDefault(entry.getKey() + "_" + l.name(), null);
            if (e instanceof ModLabel) {
                ((ModLabel) e).color = c;
            }
        }
    }

    static {
        PAGE_TITLE_X = 360.0F;
        PAGE_TITLE_Y = 815.0F;
        ELEMENT_X = 355.0F;
        ELEMENT_Y = 730.0F;
        PADDINGS_Y = Arrays.asList(55.0F, 125.0F);
        PAGE_BUTTON_X1 = 1015.0F;
        PAGE_BUTTON_X2 = 815.0F;
        PAGE_BUTTON_Y = 280.0F;

        LEXICON_X = 380.0F;
        LEXICON_Y = 720.0F;
        LEXICON_PAD_X = 400.0F;
        LEXICON_PAD_Y = -160.0F;
        BUTTON_DELTA_X1 = 200.0F;
        BUTTON_DELTA_X2 = 0.0F;
        BUTTON_DELTA_Y = -80.0F;
        WEIGHT_DELTA_X = 120.0F;
        WEIGHT_DELTA_Y = -50.0F;
    }
}
