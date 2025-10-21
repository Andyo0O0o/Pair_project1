package com.example;

import java.util.HashSet;
import java.util.Set;

/**
 * 检查表达式唯一性，处理加法和乘法交换律（如 a + b 和 b + a 视为相同）。
 * 使用规范化表达式存储在Set中，快速判重。
 */
public class UniqueChecker {
    private final Set<String> uniqueExprs; // 存储规范化表达式

    /**
     * 构造函数，初始化唯一性检查器。
     */
    public UniqueChecker() {
        this.uniqueExprs = new HashSet<>();
    }

    /**
     * 检查表达式是否唯一。
     * @param expr 表达式字符串
     * @return true 如果表达式唯一，false 如果重复
     */
    public boolean isUnique(String expr) {
        String normalized = normalize(expr);
        if (uniqueExprs.contains(normalized)) {
            return false;
        }
        uniqueExprs.add(normalized);
        return true;
    }

    /**
     * 规范化表达式，处理加法/乘法交换律。
     * @param expr 原始表达式
     * @return 规范化后的字符串
     */
    private String normalize(String expr) {
        // 简单实现：替换×为*，÷为/，按操作数排序（仅处理简单情况）
        String[] tokens = expr.replace("×", "*").replace("÷", "/").split("\\s+");
        StringBuilder normalized = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("+") || tokens[i].equals("*")) {
                // 对加法/乘法操作数排序
                if (i > 0 && i < tokens.length - 1) {
                    String[] operands = new String[]{tokens[i - 1], tokens[i + 1]};
                    java.util.Arrays.sort(operands);
                    normalized.setLength(normalized.length() - tokens[i - 1].length() - 1);
                    normalized.append(operands[0]).append(" ").append(tokens[i]).append(" ").append(operands[1]);
                    i++; // 跳过下一个操作数
                    continue;
                }
            }
            normalized.append(tokens[i]).append(" ");
        }
        return normalized.toString().trim();
    }
}