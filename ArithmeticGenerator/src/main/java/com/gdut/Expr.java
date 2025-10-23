package com.gdut;

public abstract class Expr {
    public abstract String toInfix(int parentPrec, boolean isRightChild);
    public abstract String getCanonical();
    public abstract Fraction eval();
    public abstract boolean isValid();

    // Default toInfix call
    public String toInfix() {
        return toInfix(0, false);
    }
}