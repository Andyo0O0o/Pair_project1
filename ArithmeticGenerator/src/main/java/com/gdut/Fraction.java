package com.gdut;

import java.util.Objects;

public class Fraction {
    private long num;
    private long den;

    public Fraction(long num, long den) {
        if (den == 0) throw new IllegalArgumentException("Denominator cannot be zero");
        long g = gcd(Math.abs(num), Math.abs(den));
        this.num = num / g;
        this.den = den / g;
        if (this.den < 0) {
            this.den = -this.den;
            this.num = -this.num;
        }
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public Fraction add(Fraction other) {
        long newNum = this.num * other.den + other.num * this.den;
        long newDen = this.den * other.den;
        return new Fraction(newNum, newDen);
    }

    public Fraction subtract(Fraction other) {
        long newNum = this.num * other.den - other.num * this.den;
        long newDen = this.den * other.den;
        return new Fraction(newNum, newDen);
    }

    public Fraction multiply(Fraction other) {
        long newNum = this.num * other.num;
        long newDen = this.den * other.den;
        return new Fraction(newNum, newDen);
    }

    public Fraction divide(Fraction other) {
        if (other.num == 0) throw new IllegalArgumentException("Division by zero");
        long newNum = this.num * other.den;
        long newDen = this.den * other.num;
        return new Fraction(newNum, newDen);
    }

    public int compareTo(Fraction other) {
        long diff = this.num * other.den - other.num * this.den;
        if (diff > 0) return 1;
        if (diff < 0) return -1;
        return 0;
    }

    public boolean greaterThan(Fraction other) {
        return compareTo(other) > 0;
    }

    public boolean greaterOrEqual(Fraction other) {
        return compareTo(other) >= 0;
    }

    public boolean isZero() {
        return num == 0;
    }

    public long getDenominator() {
        return den;
    }

    public boolean isInteger() {
        return den == 1;
    }

    public boolean equals(Fraction other) {
        if (other == null) return false;
        return this.num * other.den == other.num * this.den;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Fraction)) return false;
        return equals((Fraction) obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, den);
    }

    public String toString() {
        if (den == 1) return String.valueOf(num);
        long whole = num / den;
        long rem = num % den;
        if (whole == 0) return rem + "/" + den;
        if (rem == 0) return String.valueOf(whole);
        return whole + "'" + rem + "/" + den;
    }

    public static Fraction parse(String s) {
        if (s.contains("'")) {
            String[] parts = s.split("'");
            long whole = Long.parseLong(parts[0]);
            String fracPart = parts[1];
            String[] frac = fracPart.split("/");
            long fNum = Long.parseLong(frac[0]);
            long fDen = Long.parseLong(frac[1]);
            return new Fraction(whole * fDen + fNum, fDen);
        } else if (s.contains("/")) {
            String[] parts = s.split("/");
            return new Fraction(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        } else {
            return new Fraction(Long.parseLong(s), 1);
        }
    }
}