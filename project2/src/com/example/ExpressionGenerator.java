package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 生成四则运算表达式，协调约束检查和文件输出。
 * 确保表达式满足：无负数减法、除法真分数、最大3运算符、题目唯一。
 */
public class ExpressionGenerator {
    private final int numQuestions; // 题目数量
    private final int range;       // 数值范围
    private final Random rand;      // 随机数生成器
    private final Evaluator evaluator; // 表达式计算器
    private final UniqueChecker uniqueChecker; // 唯一性检查器
    private final ExerciseFileWriter fileWriter; // 文件写入器

    /**
     * 构造函数，初始化生成参数。
     * @param numQuestions 题目数量
     * @param range 数值和分母范围
     */
    public ExpressionGenerator(int numQuestions, int range) {
        this.numQuestions = numQuestions;
        this.range = range;
        this.rand = new Random();
        this.evaluator = new Evaluator();
        this.uniqueChecker = new UniqueChecker();
        this.fileWriter = new ExerciseFileWriter(); // 使用新类名，无参构造函数
    }

    /**
     * 生成题目并保存到文件（Exercises.txt 和 Answers.txt）。
     */
    public void generateAndSave() {
        List<String> exercises = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        // 循环生成直到达到所需题目数量
        while (exercises.size() < numQuestions) {
            String expr = generateExpression();
            Fraction value = evaluator.evaluate(expr);
            // 检查约束：非负结果、无除零、唯一
            if (value != null && value.isPositive() && uniqueChecker.isUnique(expr)) {
                exercises.add((exercises.size() + 1) + ". " + expr + " =");
                answers.add((answers.size() + 1) + ". " + value.format());
            }
        }

        // 批量写入文件
        fileWriter.writeToFile("Exercises.txt", exercises);
        fileWriter.writeToFile("Answers.txt", answers);
    }

    /**
     * 生成单个表达式，包含1-3个运算符，可能带括号。
     * @return 表达式字符串
     */
    private String generateExpression() {
        int opCount = rand.nextInt(3) + 1; // 1-3个运算符
        List<String> tokens = new ArrayList<>();
        tokens.add(generateOperand()); // 第一个操作数

        // 添加运算符和操作数
        for (int i = 0; i < opCount; i++) {
            tokens.add(getRandomOperator());
            tokens.add(generateOperand());
        }

        String expr = String.join(" ", tokens);
        // 50%概率添加括号
        if (opCount > 1 && rand.nextBoolean()) {
            expr = addParentheses(tokens);
        }
        return expr;
    }

    /**
     * 随机生成操作数（自然数或真分数）。
     * @return 操作数字符串
     */
    private String generateOperand() {
        if (rand.nextBoolean()) {
            // 生成自然数（0到range-1）
            return String.valueOf(rand.nextInt(range));
        } else {
            // 生成真分数（分子1到range-1，分母2到range）
            int numerator = rand.nextInt(range - 1) + 1;
            int denominator = rand.nextInt(range - 1) + 2;
            return String.format("%d/%d", numerator, denominator);
        }
    }

    /**
     * 随机选择运算符。
     * @return 运算符字符串
     */
    private String getRandomOperator() {
        String[] operators = {"+", "-", "×", "÷"};
        return operators[rand.nextInt(operators.length)];
    }

    /**
     * 随机为表达式添加合法括号。
     * @param tokens 表达式tokens
     * @return 带括号的表达式
     */
    private String addParentheses(List<String> tokens) {
        // 简单实现：随机包裹子表达式
        if (tokens.size() < 3) return String.join(" ", tokens);
        int start = rand.nextInt(tokens.size() - 2) | 1; // 确保从操作数开始
        int end = start + 2; // 至少包含一个运算
        if (end >= tokens.size()) end = tokens.size() - 1;
        List<String> newTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (i == start) newTokens.add("(");
            newTokens.add(tokens.get(i));
            if (i == end) newTokens.add(")");
        }
        return String.join(" ", newTokens);
    }
}