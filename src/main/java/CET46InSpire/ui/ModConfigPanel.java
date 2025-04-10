package CET46InSpire.ui;

import CET46InSpire.CET46Initializer;
import CET46InSpire.events.CallOfCETEvent.BookEnum;
import basemod.IUIElement;
import basemod.ModLabel;
import basemod.ModLabeledButton;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static basemod.EasyConfigPanel.*;

public class ModConfigPanel extends ModPanel {
    /**
     * pages用于标记参数的显示页数
     */
    private static List<List<String>> pages;
    private static final int configPageNum;
    private static HashMap<String, List<BookEnum>> lexiconMap;
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
    private static final float PADDING_Y;
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

    static {
        pages = new ArrayList<>();
        pages.add(Arrays.asList("darkMode", "pureFont"));
        List<String> page2 = new ArrayList<>();     // 第二页不能用Arrays.asList 因为预计将修改其内容
        page2.add("loadCET4");
        page2.add("loadCET6");
        pages.add(page2);

        configPageNum = 2;
        lexiconMap = new HashMap<>();
        lexiconData = new HashMap<>();
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

    public void initPanel() {
        // 用于初始化界面元素, 配置变量读取由类初始化完成. 须保证调用时languagePack初始化已经完成
        this.uiStrings = CardCrawlGame.languagePack.getUIString(CET46Initializer.JSON_MOD_KEY + "ConfigPanel");
        this.elementData = new HashMap<>();
        this.initUIElements();
        this.initRelicPages();
        setPage(0);
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
                        pagePos -= PADDING_Y;
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // 翻页按钮
        // TODO 修复非致命bug: 点击pageForward时ModPanel line132报错java.util.ConcurrentModificationException(不会导致游戏崩溃)
        pageForward = new ModLabeledButton(">", PAGE_BUTTON_X1, PAGE_BUTTON_Y, Settings.CREAM_COLOR, Color.WHITE,
                FontHelper.cardEnergyFont_L, this, (button) -> {this.nextPage(true);});
        pageBackward = new ModLabeledButton("<", PAGE_BUTTON_X2, PAGE_BUTTON_Y, Settings.CREAM_COLOR, Color.WHITE,
                FontHelper.cardEnergyFont_L, this, (button) -> {this.nextPage(false);});
        pageReturn = new ModLabeledButton("Save And Return", PAGE_BUTTON_X2, PAGE_BUTTON_Y, Settings.CREAM_COLOR, Color.WHITE,
                FontHelper.cardEnergyFont_L, this, (button) -> {
            try {
                // TODO 检查是否合法(主要是必须保证权重不全为零)
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
        for (Map.Entry<String, List<BookEnum>> entry: lexiconMap.entrySet()) {
            List<String> page = new ArrayList<>();
            IUIElement tmp = null;
            IUIElement base = elementData.getOrDefault(entry.getKey(), null);
            if (base == null) {
                continue;
            }
            int target = pages.size();
            // TODO 改成本地化字段
            tmp = new ModLabeledButton("LEXICON", base.getX() + 600.0F, base.getY(), Settings.CREAM_COLOR,
                    Color.WHITE, FontHelper.cardEnergyFont_L, this, (button) -> {this.setPage(target);});
            // 目前来说Relic肯定是第二页
            (pages.get(1)).add(entry.getKey() + "jump");
            elementData.put(entry.getKey() + "jump", tmp);

            for (int i = 0; i < entry.getValue().size(); i++) {
                BookEnum b = entry.getValue().get(i);
                String tmp_name = entry.getKey() + "_" + b.name();
                float x = LEXICON_X + LEXICON_PAD_X * (float) (i % 3);
                float y = LEXICON_Y + LEXICON_PAD_Y * (float) (i / 3);
                // lexicon text
                tmp = new ModLabel(uiStrings.TEXT_DICT.getOrDefault(b.name(), b.name()), x, y, this, (label) -> {});
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

    public static void addRelicPage(String varName, List<BookEnum> list, int default_) {
        // varName是挂靠的字段名, 需要在elementData里面, 否则没有效果
        lexiconMap.put(varName, list);
        for (BookEnum b: list) {
            lexiconData.put(varName + "_" + b.name(), default_);
        }
    }

    public static void addRelicPage(String varName, List<BookEnum> list) {
        addRelicPage(varName, list, 1);
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
        this.getUpdateElements().clear();
        this.getRenderElements().clear();
        if (uiStrings != null && id < uiStrings.TEXT.length && !uiStrings.TEXT[id].isEmpty()) {
            this.pageTitle.text = uiStrings.TEXT[id];
            this.addUIElement(this.pageTitle);
        }
        for (String name: pages.get(id)) {
            this.addUIElement(this.elementData.get(name));
        }
        if (this.pageNum < configPageNum) {
            // 翻页按钮
            this.addUIElement(this.pageForward);
            this.addUIElement(this.pageBackward);
        } else {
            // 返回按钮
            this.addUIElement(this.pageReturn);
        }
    }

    private IUIElement buildElement(Field field, String name) throws IllegalAccessException {
        if (field.getType() == boolean.class) {
            return new ModLabeledToggleButton(uiStrings.TEXT_DICT.get(name), ELEMENT_X, 0.0F,
                    Settings.CREAM_COLOR, FontHelper.charDescFont, (Boolean)field.get(null), this, (label) -> {},
                    (button) -> {saveVar(button.enabled, field, s -> {field.set(null, Boolean.parseBoolean(s));});});
        }
        return null;
    }

    private void saveVar(Object var, Field field, ConfigField.FieldSetter setter) {
        try {
            setter.set(var.toString());
            this.config.setString(field.getName(), field.get(null).toString());
            this.config.save();
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        PAGE_TITLE_X = 360.0F;
        PAGE_TITLE_Y = 815.0F;
        ELEMENT_X = 355.0F;
        ELEMENT_Y = 730.0F;
        PADDING_Y = 55.0F;
        PAGE_BUTTON_X1 = 1015.0F;
        PAGE_BUTTON_X2 = 915.0F;
        PAGE_BUTTON_Y = 280.0F;

        LEXICON_X = 380.0F;
        LEXICON_Y = 720.0F;
        LEXICON_PAD_X = 400.0F;
        LEXICON_PAD_Y = 120.0F;
        BUTTON_DELTA_X1 = 200.0F;
        BUTTON_DELTA_X2 = 0.0F;
        BUTTON_DELTA_Y = -80.0F;
        WEIGHT_DELTA_X = 120.0F;
        WEIGHT_DELTA_Y = -50.0F;
    }
}
