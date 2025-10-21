package com.example;

/**
 * 表示真分数或带分数的类，支持四则运算和格式化输出。
 * 自动简化分数，处理自然数、真分数和带分数格式（如3/5, 2'3/8）。
 */
public class Fraction {
    private final long numerator;   // 分子
    private final long denominator; // 分母

    public static final Fraction ZERO = new Fraction(0, 1);

    /**
     * 构造函数，初始化分数并自动简化。
     * @param numerator 分子
     * @param denominator 分母（不为0）
     * @throws IllegalArgumentException 如果分母为0
     */
    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        // 规范化符号：负号移到分子，分母保持正
        long adjustedNumerator = numerator;
        long adjustedDenominator = denominator;
        if (denominator < 0) {
            adjustedNumerator = -numerator;
            adjustedDenominator = -denominator;
        }
        // 使用GCD简化分数
        long gcd = gcd(Math.abs(adjustedNumerator), Math.abs(adjustedDenominator));
        this.numerator = adjustedNumerator / gcd;
        this.denominator = adjustedDenominator / gcd;
    }

    /**
     * 加法运算。
     * @param other 另一个分数
     * @return 结果分数
     */
    public Fraction add(Fraction other) {
        long newNum = this.numerator * other.denominator + other.numerator * this.denominator;
        long newDen = this.denominator * other.denominator;
        return new Fraction(newNum, newDen);
    }

    /**
     * 减法运算，确保结果非负。
     * @param other 另一个分数
     * @return 结果分数，或null（如果结果负数）
     */
    public Fraction subtract(Fraction other) {
        long newNum = this.numerator * other.denominator - other.numerator * this.denominator;
        long newDen = this.denominator * other.denominator;
        Fraction result = new Fraction(newNum, newDen);
        return result.isPositive() ? result : null;
    }

    /**
     * 乘法运算。
     * @param other 另一个分数
     * @return 结果分数
     */
    public Fraction multiply(Fraction other) {
        return new Fraction(this.numerator * other.numerator, this.denominator * other.denominator);
    }

    /**
     * 除法运算，确保除数非0。
     * @param other 另一个分数
     * @return 结果分数，或null（如果除数为0）
     */
    public Fraction divide(Fraction other) {
        if (other.numerator == 0) {
            return null;
        }
        return new Fraction(this.numerator * other.denominator, this.denominator * other.numerator);
    }

    /**
     * 格式化分数为字符串，支持自然数（5/1 -> 5）、真分数（3/5）、带分数（11/4 -> 2'3/4）。
     * @return 格式化后的字符串
     */
    public String format() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }
        if (numerator >= denominator) {
            long whole = numerator / denominator;
            long remainder = numerator % denominator;
            if (remainder == 0) {
                return String.valueOf(whole);
            }
            return String.format("%d'%d/%d", whole, remainder, denominator);
        }
        return String.format("%d/%d", numerator, denominator);
    }

    /**
     * 检查分数是否非负。
     * @return true 如果分数非负
     */
    public boolean isPositive() {
        return numerator >= 0 && denominator > 0;
    }

    /**
     * 比较两个分数大小。
     * @param other 另一个分数
     * @return 负数（小于）、0（等于）、正数（大于）
     */
    public int compareTo(Fraction other) {
        long left = this.numerator * other.denominator;
        long right = other.numerator * this.denominator;
        return Long.compare(left, right);
    }

    /**
     * 计算最大公约数（GCD）用于简化分数。
     * @param a 第一个数
     * @param b 第二个数
     * @return GCD
     */
    private long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}