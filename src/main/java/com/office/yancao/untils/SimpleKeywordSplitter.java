package com.office.yancao.untils;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SimpleKeywordSplitter {

    // 常用停用词
    private static final Set<String> STOP_WORDS;

    // 静态代码块初始化集合（Java 8 初始化不可变静态Set的标准方式）
    static {
        // 1. 先创建可修改的临时HashSet，添加所有停用词
        Set<String> tempStopWords = new HashSet<>();
        tempStopWords.add("关于");
        tempStopWords.add("有关");
        tempStopWords.add("进行");
        tempStopWords.add("工作");
        tempStopWords.add("情况");
        tempStopWords.add("问题");
        tempStopWords.add("通知");
        tempStopWords.add("要求");

        // 2. 封装为不可变集合，和原Set.of()一样禁止修改
        STOP_WORDS = Collections.unmodifiableSet(tempStopWords);
    }

    /**
     * 智能分词（保持简单但更智能）
     */
    public List<String> splitKeyword(String text) {
        Set<String> result = new HashSet<>();

        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 标准化
        text = normalize(text);

        // 2. 按标点切分（保留原逻辑）
        String[] parts = text.split("[，。,.、\\s]+");

        // 3. 处理每个部分
        for (String part : parts) {
            if (part.length() < 2) continue;

            // 过滤停用词
            if (STOP_WORDS.contains(part)) continue;

            // 添加完整词
            result.add(part);

            // 对长词（≥4字）进行子词拆分
            if (part.length() >= 4) {
                result.addAll(splitLongWord(part));
            }
        }

        // 按长度降序返回（长词优先）
        return new ArrayList<>(result).stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .collect(Collectors.toList());
    }

    /**
     * 拆分长词：生成2-4字的子串
     */
    private List<String> splitLongWord(String word) {
        List<String> subWords = new ArrayList<>();
        int len = word.length();

        // 滑动窗口生成子串
        for (int i = 0; i < len - 1; i++) {
            for (int j = i + 2; j <= Math.min(i + 4, len); j++) {
                String sub = word.substring(i, j);
                // 只保留有意义的子串（简单规则）
                if (isMeaningful(sub)) {
                    subWords.add(sub);
                }
            }
        }
        return subWords;
    }

    /**
     * 简单标准化
     */
    private String normalize(String text) {
        return text.trim()
                .replaceAll("\\s+", " ")      // 合并空格
                .replaceAll("[（）()【】\\[\\]]", "")  // 移除括号
                .toLowerCase();
    }

    /**
     * 简单判断是否有意义（过滤纯数字、单字重复等）
     */
    private boolean isMeaningful(String word) {
        if (word.length() < 2) return false;
        if (word.matches("\\d+")) return false;  // 纯数字
        if (word.matches("(.)\\1+")) return false; // 如"哈哈"
        return true;
    }
}
