package CET46InSpire.helpers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JapaneseKanaConfuser {
    // 清音-浊音-半浊音对应表（支持三向替换）
    private static final String[][][] VOICED_TRIPLES = {
            // か行
            {{"か", "が"}}, {{"き", "ぎ"}}, {{"く", "ぐ"}}, {{"け", "げ"}}, {{"こ", "ご"}},
            // さ行
            {{"さ", "ざ"}}, {{"し", "じ"}}, {{"す", "ず"}}, {{"せ", "ぜ"}}, {{"そ", "ぞ"}},
            // た行
            {{"た", "だ"}}, {{"ち", "ぢ"}}, {{"つ", "づ"}}, {{"て", "で"}}, {{"と", "ど"}},
            // は行（特殊：三向替换）
            {{"は", "ば", "ぱ"}}, {{"ひ", "び", "ぴ"}}, {{"ふ", "ぶ", "ぷ"}}, {{"へ", "べ", "ぺ"}}, {{"ほ", "ぼ", "ぽ"}},
            // 片假名か行
            {{"カ", "ガ"}}, {{"キ", "ギ"}}, {{"ク", "グ"}}, {{"ケ", "ゲ"}}, {{"コ", "ゴ"}},
            // 片假名さ行
            {{"サ", "ザ"}}, {{"シ", "ジ"}}, {{"ス", "ズ"}}, {{"セ", "ゼ"}}, {{"ソ", "ゾ"}},
            // 片假名た行
            {{"タ", "ダ"}}, {{"チ", "ヂ"}}, {{"ツ", "ヅ"}}, {{"テ", "デ"}}, {{"ト", "ド"}},
            // 片假名は行（三向替换）
            {{"ハ", "バ", "パ"}}, {{"ヒ", "ビ", "ピ"}}, {{"フ", "ブ", "プ"}}, {{"ヘ", "ベ", "ペ"}}, {{"ホ", "ボ", "ポ"}}
    };

    // 拗音对应表
    private static final String[][] SMALL_KANA_PAIRS = {
            {"や", "ゃ"}, {"ゆ", "ゅ"}, {"よ", "ょ"},
            {"ヤ", "ャ"}, {"ユ", "ュ"}, {"ヨ", "ョ"},
            //{"あ", "ぁ"}, {"い", "ぃ"}, {"う", "ぅ"}, {"え", "ぇ"}, {"お", "ぉ"},
            //{"ア", "ァ"}, {"イ", "ィ"}, {"ウ", "ゥ"}, {"Э", "ェ"}, {"オ", "ォ"},
            {"つ", "っ"}, {"ツ", "ッ"}
    };

    // 类似假名对应表
    private static final String[][] SIMILAR_KANA = {
            {"ね", "れ"}, {"わ", "れ"}, {"る", "ろ"}, {"め", "ぬ"},
            {"ノ", "ヌ"}, {"ソ", "ン"}, {"シ", "ツ"}, {"コ", "ユ"},
            {"あ", "お"}, {"い", "り"}, {"う", "つ"}, {"え", "へ"}, {"お", "を"},
            {"ク", "ケ"}, {"タ", "ナ"}, {"ヒ", "ビ"}, {"フ", "ワ"}
    };

    public static List<String> generateConfusingKana(String correctAnswer, int n, List<String> confusingList) {
        if (correctAnswer == null || correctAnswer.isEmpty()) {
            return null;
        }
        Set<String> existList = new HashSet<>();
        existList.add(correctAnswer);
        Random random = new Random();
        for (int i = 0; i < n ;i ++) {
            String confused = confuseKana(correctAnswer, random, existList);
            if (confused != null && !confused.equals(correctAnswer)) {
                confusingList.add(confused);
                existList.add(confused);
            }
        }
        return confusingList;
    }

    static final int STRATEGY_SIZE = 2;

    /**
     * @return 返回null表示所有策略均无法混淆
     */
    private static String confuseKana(String kana, Random random, Collection<String> existList) {
        if (kana.isEmpty()) {
            return null;
        }
        String input = kana;
        String subResult = null;
        String allResult = null;
        // 随机1~2次
        int repeat = 1 + random.nextInt(2);
        for (; repeat > 0; repeat--) {
            List<Integer> tryStrategyOrderedList = IntStream.range(0, STRATEGY_SIZE)
                    .mapToObj(it -> it)
                    .collect(Collectors.toList());
            // 以随机顺序尝试所有策略
            Collections.shuffle(tryStrategyOrderedList);
            for (Integer strategy : tryStrategyOrderedList) {
                subResult = confuseKana(input, random, strategy, existList);
                if (subResult != null) {
                    allResult = subResult;
                    input = subResult;
                    break;
                }
            }
        }

        return allResult;
    }
    /**
     * @return 返回null表示输入策略无法混淆
     */
    private static String confuseKana(String kana, Random random, int strategy, Collection<String> existList) {
        String result;
        List<Integer> tryPosOrderedList = IntStream.range(0, kana.length())
                .mapToObj(it -> it)
                .collect(Collectors.toList());
        Collections.shuffle(tryPosOrderedList);

        // 以随机顺序尝试所有位置
        for (int pos : tryPosOrderedList) {
            String target = kana.substring(pos, pos + 1);

            switch (strategy) {
                case 0: // 清浊音变换
                    for (String[][] group : VOICED_TRIPLES) {
                        for (String[] pair : group) {
                            // 遍历所有可能的替换
                            for (int i = 0; i < pair.length; i++) {
                                if (pair[i].equals(target)) {
                                    // 尝试所有其他变体
                                    for (int j = 0; j < pair.length; j++) {
                                        if (j != i) {
                                            String candidate = replaceChar(kana, pos, pair[j]);
                                            if (!candidate.equals(kana)) {
                                                result = candidate;
                                                if (!existList.contains(result)) {
                                                    return result;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;

                case 1: // 拗音变换
                    if (pos == 0) {
                        break;
                    }
                    for (String[] pair : SMALL_KANA_PAIRS) {
                        if (pair[0].equals(target)) {
                            result = replaceChar(kana, pos, pair[1]);
                            if (!existList.contains(result)) {
                                return result;
                            }
                        } else if (pair[1].equals(target)) {
                            result = replaceChar(kana, pos, pair[0]);
                            if (!existList.contains(result)) {
                                return result;
                            }
                        }
                    }
                    break;

/*                case 2: // 类似假名替换
                    for (String[] pair : SIMILAR_KANA) {
                        if (pair[0].equals(target)) {
                            result = replaceChar(kana, pos, pair[1]);
                            if (!existList.contains(result)) {
                                return result;
                            }
                        } else if (pair[1].equals(target)) {
                            result = replaceChar(kana, pos, pair[0]);
                            if (!existList.contains(result)) {
                                return result;
                            }
                        }
                    }
                    break;*/
/*                case 3: // 随机替换一个假名
                    String randomKana = getRandomSimilarKana(target, random);
                    if (randomKana != null) {
                        result = replaceChar(kana, pos, randomKana);
                        if (!existList.contains(result)) {
                            return result;
                        }
                    }
                    break;*/
                default:
            }
        }

        return null; // 无法混淆
    }

    private static String replaceChar(String str, int pos, String replacement) {
        return str.substring(0, pos) + replacement + str.substring(pos + 1);
    }

    private static String getRandomSimilarKana(String kana, Random random) {
        // 这里可以添加更多类似的假名对应关系
        switch (kana) {
            case "あ": return random.nextBoolean() ? "お" : "め";
            case "い": return random.nextBoolean() ? "り" : "ぃ";
            case "う": return random.nextBoolean() ? "つ" : "ゔ";
            case "え": return "へ";
            case "お": return random.nextBoolean() ? "あ" : "を";
            case "か": return "が";
            case "き": return "ぎ";
            case "く": return "ぐ";
            case "け": return "げ";
            case "こ": return "ご";
            case "さ": return "ざ";
            case "し": return "じ";
            case "す": return "ず";
            case "せ": return "ぜ";
            case "そ": return "ぞ";
            case "た": return "だ";
            case "ち": return "ぢ";
            case "つ": return "づ";
            case "て": return "で";
            case "と": return "ど";
            case "は": return random.nextBoolean() ? "ば" : "ぱ";
            case "ひ": return random.nextBoolean() ? "び" : "ぴ";
            case "ふ": return random.nextBoolean() ? "ぶ" : "ぷ";
            case "へ": return random.nextBoolean() ? "べ" : "ぺ";
            case "ほ": return random.nextBoolean() ? "ぼ" : "ぽ";
            case "や": return "ゃ";
            case "ゆ": return "ゅ";
            case "よ": return "ょ";
            case "わ": return "れ";
            case "を": return "お";
            default: return null;
        }
    }

    public static void main(String[] args) {
        // 测试用例
        String[] testCases = {"かきくけこ", "しんぶん", "あいうえお", "たちつてと", "はひふへほ", "ん"};

        for (String testCase : testCases) {
            System.out.println("\n测试输入: " + testCase);
            List<String> confusingAnswers = generateConfusingKana(testCase, 5, new ArrayList<>());

            if (confusingAnswers == null) {
                System.out.println("无法生成混淆答案");
            } else {
                System.out.println("生成混淆答案 (" + confusingAnswers.size() + "个):");
                for (String answer : confusingAnswers) {
                    int diff = countCharDifference(answer, testCase);
                    System.out.println(answer + ", diff = " + diff);
                }
            }
        }
    }

    public static int countCharDifference(String s1, String s2) {
        int length = Math.min(s1.length(), s2.length());
        int diff = 0;
        for (int i = 0; i < length; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                diff++;
            }
        }
        diff += Math.abs(s1.length() - s2.length());
        return diff;
    }
}