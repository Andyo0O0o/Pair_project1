package com.example;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析并计算四则运算表达式的值，返回Fraction。
 * 使用栈解析器处理括号和运算符优先级，确保无负数结果。
 */
public class Evaluator {
    /**
     * 计算表达式值，验证约束（无负数、除法真分数）。
     * @param expr 表达式字符串（e.g., "3 + 5/2 × (1 - 0)")
     * @return 计算结果（Fraction），或null（无效或负数）
     */
    public Fraction evaluate(String expr) {
        try {
            // 分割tokens并处理括号
            String[] tokens = expr.split("\\s+");
            Stack<Fraction> values = new Stack<>();
            Stack<String> operators = new Stack<>();

            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                if (token.equals("(")) {
                    operators.push(token);
                } else if (token.equals(")")) {
                    while (!operators.peek().equals("(")) {
                        applyOperator(values, operators.pop());
                    }
                    operators.pop(); // 移除"("
                } else if (isOperator(token)) {
                    // 处理运算符优先级
                    while (!operators.isEmpty() && !operators.peek().equals("(") &&
                            hasHigherPrecedence(operators.peek(), token)) {
                        applyOperator(values, operators.pop());
                    }
                    operators.push(token);
                } else {
                    // 处理操作数（自然数或分数）
                    values.push(parseOperand(token));
                }
            }

            // 处理剩余运算符
            while (!operators.isEmpty()) {
                applyOperator(values, operators.pop());
            }

            Fraction result = values.pop();
            return result.isPositive() ? result : null;
        } catch (Exception e) {
            return null; // 无效表达式或除零
        }
    }

    /**
     * 检查是否是运算符。
     * @param token 输入token
     * @return true 如果是运算符
     */
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("×") || token.equals("÷");
    }

    /**
     * 检查运算符优先级。
     * @param op1 栈顶运算符
     * @param op2 当前运算符
     * @return true 如果op1优先级高于或等于op2
     */
    private boolean hasHigherPrecedence(String op1, String op2) {
        if (op1.equals("×") || op1.equals("÷")) return true;
        if (op2.equals("×") || op2.equals("÷")) return false;
        return true;
    }

    /**
     * 应用运算符，执行计算。
     * @param values 操作数栈
     * @param op 运算符
     */
    private void applyOperator(Stack<Fraction> values, String op) {
        if (values.size() < 2) throw new IllegalStateException("Invalid expression");
        Fraction b = values.pop();
        Fraction a = values.pop();
        Fraction result;
        switch (op) {
            case "+":
                result = a.add(b);
                break;
            case "-":
                result = a.subtract(b);
                if (result == null) throw new IllegalStateException("Negative result");
                break;
            case "×":
                result = a.multiply(b);
                break;
            case "÷":
                result = a.divide(b);
                if (result == null) throw new IllegalStateException("Division by zero");
                break;
            default:
                throw new IllegalStateException("Unknown operator: " + op);
        }
        values.push(result);
    }

    /**
     * 解析操作数（自然数或分数）。
     * @param token 操作数字符串
     * @return Fraction对象
     */
    private Fraction parseOperand(String token) {
        if (token.contains("'")) {
            // 带分数：e.g., 2'3/4
            String[] parts = token.split("'");
            int whole = Integer.parseInt(parts[0]);
            String[] fracParts = parts[1].split("/");
            int num = Integer.parseInt(fracParts[0]);
            int den = Integer.parseInt(fracParts[1]);
            return new Fraction(whole * den + num, den);
        } else if (token.contains("/")) {
            // 真分数：e.g., 3/5
            String[] parts = token.split("/");
            return new Fraction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            // 自然数：e.g., 5
            return new Fraction(Integer.parseInt(token), 1);
        }
    }
}