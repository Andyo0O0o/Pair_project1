package com.gdut;

public class Leaf extends Expr {
    private Fraction value;

    public Leaf(Fraction value) {
        this.value = value;
    }

    @Override
    public String toInfix(int parentPrec, boolean isRightChild) {
        return value.toString();
    }

    @Override
    public String getCanonical() {
        return value.toString();
    }

    @Override
    public Fraction eval() {
        return value;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}