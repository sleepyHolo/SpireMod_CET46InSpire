package CET46InSpire.savedata;

import com.badlogic.gdx.math.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CorrectionNote {
    private static final Logger logger = LogManager.getLogger(CorrectionNote.class.getName());
    public int totalCount;
    public HashMap<String, Integer> items;
    public ArrayList<String> removedItems;

    public CorrectionNote() {
        this.totalCount = 0;
        this.items = new HashMap<>();
        this.removedItems = new ArrayList<>();
    }

    public void addItem(String key) {
        this.totalCount++;
        if (this.items.containsKey(key)) {
            this.items.put(key, this.items.get(key) + 1);
        } else {
            this.items.put(key, 1);
        }
    }

    public void reduceItem(String key) {
        this.totalCount--;
        if (this.items.containsKey(key)) {
            int weight = this.items.get(key) - 1;
            if (weight == 0) {
                this.items.remove(key);
                this.removedItems.add(key);
            }
        } else {
            logger.info("No such key: {}", key);
        }
    }

    public String rndGetId() {
        if (this.totalCount == 0) {
            return "";
        }
        int count = 0;
        int target = MathUtils.random(0, this.totalCount - 1);
        logger.info("target: {}", target);
        for (Map.Entry<String, Integer> entry : this.items.entrySet()) {
            count += entry.getValue();
            if (count >= target) {
                logger.info("index: {}", entry.getKey());
                return entry.getKey();
            }
        }
        logger.info("wtf? why no found?");
        return "";
    }

    public void outItem() {
        logger.info("Item(debug): {}", this.items.toString());
    }

}
