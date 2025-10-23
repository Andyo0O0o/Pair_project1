package com.gdut;

public class Binary extends Expr {
    private String op;
    private Expr left;
    private Expr right;

    public Binary(String op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    private int getPrec() {
        if (op.equals("+") || op.equals("-")) return 1;
        return 2;
    }

    private boolean isComm() {
        return op.equals("+") || op.equals("*");
    }

    @Override
    public String toInfix(int parentPrec, boolean isRightChild) {
        int myPrec = getPrec();
        String leftStr = left.toInfix(myPrec, false);
        String rightStr = right.toInfix(myPrec, true);
        String inner = leftStr + " " + op + " " + rightStr;
        boolean needParen = (myPrec < parentPrec) || (myPrec == parentPrec && isRightChild);
        return needParen ? "(" + inner + ")" : inner;
    }

    @Override
    public String getCanonical() {
        String leftCan = left.getCanonical();
        String rightCan = right.getCanonical();
        String opStr = " " + op + " ";
        String s = "(" + leftCan + opStr + rightCan + ")";
        if (isComm()) {
            String alt = "(" + rightCan + opStr + leftCan + ")";
            return s.compareTo(alt) < 0 ? s : alt;
        }
        return s;
    }

    @Override
    public Fraction eval() {
        Fraction l = left.eval();
        Fraction r = right.eval();
        switch (op) {
            case "+": return l.add(r);
            case "-": return l.subtract(r);
            case "*": return l.multiply(r);
            case "/": return l.divide(r);
            default: throw new IllegalArgumentException("Unknown op: " + op);
        }
    }

    @Override
    public boolean isValid() {
        if (!left.isValid() || !right.isValid()) return false;
        Fraction lVal = left.eval();
        Fraction rVal = right.eval();
        if (op.equals("-")) {
            return lVal.greaterOrEqual(rVal);
        } else if (op.equals("/")) {
            if (rVal.isZero()) return false;
            Fraction quot = lVal.divide(rVal);
            return !quot.isInteger();
        }
        return true;
    }
}