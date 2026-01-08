package com.office.yancao.untils;

import com.office.yancao.entity.admin.AssessmentRule;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SimpleRuleCache {

    // 使用简单的LRU缓存
    private Map<String, List<AssessmentRule>> cache =
            new LinkedHashMap<String, List<AssessmentRule>>(100, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > 100;  // 最多缓存100个查询
                }
            };

    private final Object lock = new Object();

    /**
     * 获取缓存
     */
    public List<AssessmentRule> get(String ruleType, String keyword) {
        String key = buildKey(ruleType, keyword);
        synchronized (lock) {
            return cache.get(key);
        }
    }

    /**
     * 设置缓存
     */
    public void put(String ruleType, String keyword, List<AssessmentRule> rules) {
        String key = buildKey(ruleType, keyword);
        synchronized (lock) {
            cache.put(key, rules);
        }
    }

    /**
     * 清除缓存
     */
    public void clear() {
        synchronized (lock) {
            cache.clear();
        }
    }

    private String buildKey(String ruleType, String keyword) {
        return ruleType + ":" + keyword.hashCode();
    }
}