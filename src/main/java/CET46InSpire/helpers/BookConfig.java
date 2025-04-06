package CET46InSpire.helpers;

import CET46InSpire.events.CallOfCETEvent.BookEnum;
import CET46InSpire.ui.CET46Panel;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

public class BookConfig {
    public BookEnum bookEnum;
    public List<BookEnum> lowerLevelBooks;
    public Supplier<AbstractRelic> relicSupplier;

    public BookConfig(BookEnum bookEnum, List<BookEnum> lowerLevelBooks, Supplier<AbstractRelic> relicSupplier) {
        this.bookEnum = bookEnum;
        this.lowerLevelBooks = lowerLevelBooks;
        this.relicSupplier = relicSupplier;
    }

    public boolean needNotLoad() {
        try {
            Field field = CET46Panel.class.getField("load" + this.bookEnum.name());
            return !field.getBoolean(null);  // 一定是静态字段
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

}
