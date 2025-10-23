package com.gdut;  // 你的包名

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FractionTest {
    @Test
    void testAddProperFractions() {  // 用例1: 真分数加法
        Fraction a = new Fraction(1, 6);
        Fraction b = new Fraction(1, 8);
        Fraction expected = new Fraction(7, 24);
        assertEquals(expected, a.add(b));  // 预期: 7/24，手算验证
    }

    @Test
    void testSubtractNoNegative() {  // 用例2: 减法无负数
        Fraction a = new Fraction(3, 4);
        Fraction b = new Fraction(1, 4);
        Fraction expected = new Fraction(1, 2);
        assertTrue(a.greaterOrEqual(b));  // 先检查 >=
        assertEquals(expected, a.subtract(b));
    }

    @Test
    void testDivideToProperFraction() {  // 用例3: 除法结果真分数
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);
        Fraction result = a.divide(b);
        assertFalse(result.isInteger());  // 非整数
        assertEquals(new Fraction(3, 2), result);
    }

    @Test
    void testMixedNumberParse() {  // 用例4: 混合数解析
        Fraction f = Fraction.parse("2'1/2");
        assertEquals(new Fraction(5, 2), f);  // 2 + 1/2 = 5/2
    }
}