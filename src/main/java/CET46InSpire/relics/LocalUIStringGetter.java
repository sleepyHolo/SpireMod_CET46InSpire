package CET46InSpire.relics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocalUIStringGetter implements UIStringGetter {

    Map<String, UIStrings> resultMap;

    public LocalUIStringGetter(String fileName) {
        this.resultMap = new HashMap<>();

        Gson gson = new Gson();

        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName)))) {

            // 2. 定义目标类型：Map<String, UIStrings>
            // 注意：TypeToken 的 {} 后面有一对括号，这实际上是创建了一个匿名内部类
            Type mapType = new TypeToken<Map<String, UIStrings>>() {
            }.getType();

            // 3. 执行反序列化
            this.resultMap = gson.fromJson(reader, mapType);
        } catch (Exception e) {
            QuizRelic.logger.error(e);
        }
    }

    @Override
    public UIStrings getUIString(String key) {
        return resultMap.get(key);
    }
}
