package com.gdut;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExprTest {
    @Test
    void testCanonicalUniqueness() {  // 用例5: 唯一性（交换+）
        Expr e1 = new Binary("+", new Leaf(new Fraction(1,1)), new Leaf(new Fraction(2,1)));
        Expr e2 = new Binary("+", new Leaf(new Fraction(2,1)), new Leaf(new Fraction(1,1)));
        assertEquals(e1.getCanonical(), e2.getCanonical());  // 相同规范
    }

    @Test
    void testInfixWithParens() {  // 用例6: infix输出带括号
        Expr e = new Binary("+", new Leaf(new Fraction(1,1)),
                new Binary("-", new Leaf(new Fraction(3,1)), new Leaf(new Fraction(2,1))));
        assertEquals("1 + (3 - 2)", e.toInfix());
    }

    @Test
    void testEvalWithOpsLimit() {  // 用例7: eval() 3运算符内
        Expr inner = new Binary("/", new Leaf(new Fraction(3,1)), new Leaf(new Fraction(4,1)));
        Expr mid = new Binary("*", new Leaf(new Fraction(2,1)), inner);
        Expr outer = new Binary("+", new Leaf(new Fraction(1,1)), mid);
        assertEquals(new Fraction(5, 2), outer.eval());  // 修正：1 + 2*(3/4) = 5/2 = 2'1/2
    }
}