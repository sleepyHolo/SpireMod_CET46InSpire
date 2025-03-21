package CET46InSpire.helpers;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class ArrayListHelper {
    public static ArrayList<String> choose(ArrayList<String> list, int max_num) {
        if (list.size() <= max_num) {
            return list;
        }
        ArrayList<String> tmp = new ArrayList<>();
        for (int i = 0; i < max_num; i++) {
            int target = MathUtils.random(list.size() - 1);
            tmp.add(list.remove(target));
        }
        return tmp;
    }

    public static ArrayList<String> shuffle(ArrayList<String> list) {
        ArrayList<String> tmp = new ArrayList<>();
        while (!list.isEmpty()) {
            int target = MathUtils.random(list.size() - 1);
            tmp.add(list.remove(target));
        }
        return tmp;
    }
}
