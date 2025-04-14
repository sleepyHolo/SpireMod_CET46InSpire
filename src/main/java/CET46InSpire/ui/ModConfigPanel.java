package CET46InSpire.ui;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.helpers.BookConfig.LexiconEnum;
import basemod.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static basemod.EasyConfigPanel.ConfigField.FieldSetter;

public class ModConfigPanel extends ModPanel {
    /**
     * pages用于标记参数的显示页数
     */
    private static List<List<String>> pages;
    private static final int configPageNum;
    private static HashMap<String, List<LexiconEnum>> lexiconMap;
    private int pageNum = 0;
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
    public static int band4RateIn6 = 50;
    public static int maxAnsNum = 3;
    public static boolean loadCET4 = true;
    public static boolean loadCET6 = true;
    public static boolean loadN5 = true;
    public static boolean loadN4 = true;
    public static boolean loadN3 = true;
    public static boolean loadN2 = true;
    public static boolean loadN1 = true;

    /**
     * 这个是遗物对应的词库权重
     * lexiconData Map<RelicName_LexiconName, Weight>>>
     */
    public static HashMap<String, Integer> lexiconData;
    public static HashMap<BookEnum, HashMap<LexiconEnum, Integer>> relicLexicon;

    static {
        pages = new ArrayList<>();
        pages.add(Arrays.asList("darkMode", "pureFont", "fastMode", "casualMode", "ignoreCheck", "showLexicon"));
        List<String> page2 = new ArrayList<>();     // 第二页不能用Arrays.asList 因为预计将修改其内容
        page2.add("loadCET4");
        page2.add("loadN1");
        pages.add(page2);

        configPageNum = 2;
        lexiconMap = new HashMap<>();
        lexiconData = new HashMap<>();
        relicLexicon = new HashMap<>();
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

    public static Map<LexiconEnum, Integer> getRelicWeights(BookEnum b) {
        return relicLexicon.getOrDefault(b, new HashMap<>());
    }

    public void initPanel() {
        // 用于初始化界面元素, 配置变量读取由类初始化完成. 须保证调用时languagePack初始化已经完成
        this.uiStrings = CardCrawlGame.languagePack.getUIString(CET46Initializer.JSON_MOD_KEY + "ConfigPanel");
        this.elementData = new HashMap<>();
        this.initUIElements();
        this.initRelicPages();
        this.setPage(0);
        this.checkReset();
        this.updateWeights();   // 保证更新词库权重
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
        }
    }

    public static void addRelicPage(BookEnum relicBook, List<LexiconEnum> list, int default_) {
        relicLexicon.put(relicBook, new HashMap<>());
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
        List<IUIElement> tmp = new ArrayList<>();
        if (uiStrings != null && id < uiStrings.TEXT.length && !uiStrings.TEXT[id].isEmpty()) {
            this.pageTitle.text = uiStrings.TEXT[id];
            tmp.add(this.pageTitle);
        }
        for (String name: pages.get(id)) {
            tmp.add(this.elementData.get(name));
        }
        if (this.pageNum < configPageNum) {
            // 翻页按钮
            tmp.add(this.pageForward);
            tmp.add(this.pageBackward);
        } else {
            // 返回按钮
            tmp.add(this.pageReturn);
        }
        this.resetElements(tmp);
        // update weight display
        if (id == 1) {
            this.updateWeights();
        }
    }

    private IUIElement buildElement(Field field, String name) throws IllegalAccessException {
        if (field.getType() == boolean.class) {
            // load
            field.set(null, Boolean.parseBoolean(this.config.getString(field.getName())));
            return new ModLabeledToggleButton(uiStrings.TEXT_DICT.get(name), ELEMENT_X, 0.0F,
                    Settings.CREAM_COLOR, FontHelper.charDescFont, (Boolean)field.get(null), this, (label) -> {},
                    (button) -> {saveVar(button.enabled, field, s -> {field.set(null, Boolean.parseBoolean(s));});});
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
        }

    }

    private void updateWeights() {
        // 开摆了, 直接一坨循环搞定得了, 自己搓优化不如求大佬重写逻辑
        for (Map.Entry<String, List<LexiconEnum>> entry: lexiconMap.entrySet()) {
            IUIElement element = elementData.getOrDefault(entry.getKey() + "_display", null);
            if (!(element instanceof ModLabel)) {
                continue;
            }
            int total = 0;
            List<LexiconEnum> notZeroList = new ArrayList<>();
            for (LexiconEnum l: entry.getValue()) {
                int tmp = lexiconData.getOrDefault(entry.getKey() + "_" + l.name(), 0);
                if (tmp != 0) {
                    notZeroList.add(l);
                    total += tmp;
                }
            }
            float k = total == 0 ? 0.0F : 100.0F / total;
            StringBuilder sb = new StringBuilder();
            BookEnum relic = BookEnum.valueOf(entry.getKey().substring(4));     // 去掉前面的 load
            HashMap<LexiconEnum, Integer> tmpMap = new HashMap<>();
            // update text
            for (LexiconEnum l: notZeroList) {
                tmpMap.put(l, lexiconData.get(entry.getKey() + "_" + l.name()));
                sb.append(uiStrings.TEXT_DICT.getOrDefault(l.name(), l.name())).append(": ");
                sb.append(Math.round(k * lexiconData.get(entry.getKey() + "_" + l.name()))).append("%. ");
            }
            relicLexicon.put(relic, tmpMap);
            ((ModLabel) element).text = sb.toString();

        }
    }

    private boolean checkWeights() {
        for (Map.Entry<String, List<LexiconEnum>> entry: lexiconMap.entrySet()) {
            int total = 0;
            for (LexiconEnum l: entry.getValue()) {
                total += lexiconData.getOrDefault(entry.getKey() + "_" + l.name(), 0);
            }
            if (total == 0) {
                this.updateColor(entry, Color.RED);
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
