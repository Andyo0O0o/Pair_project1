package com.gdut;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ArithmeticGenerator {
    private static final String[] OPS = {"+", "-", "*", "/"};

    public static void main(String[] args) {
        int numProblems = 10;
        int range = -1;
        String exerciseFile = null;
        String answerFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n") && i + 1 < args.length) {
                numProblems = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-r") && i + 1 < args.length) {
                range = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-e") && i + 1 < args.length) {
                exerciseFile = args[++i];
            } else if (args[i].equals("-a") && i + 1 < args.length) {
                answerFile = args[++i];
            }
        }

        if (exerciseFile != null && answerFile != null) {
            grade(exerciseFile, answerFile);
            return;
        }

        if (range == -1) {
            System.err.println("Error: -r parameter is required for generation mode.");
            System.err.println("Usage:");
            System.err.println("  java ArithmeticGenerator -n <num> -r <range>");
            System.err.println("  java ArithmeticGenerator -e <exercises.txt> -a <answers.txt>");
            return;
        }

        generate(numProblems, range);
    }

    static void generate(int n, int r) {
        List<Fraction> leaves = new ArrayList<>();
        // Natural numbers
        for (int i = 0; i < r; i++) {
            leaves.add(new Fraction(i, 1));
        }
        // Proper fractions
        for (int den = 2; den <= r; den++) {
            for (int num = 1; num < den; num++) {
                leaves.add(new Fraction(num, den));
            }
        }
        // Mixed fractions
        for (int whole = 1; whole < r; whole++) {
            for (int den = 2; den <= r; den++) {
                for (int num = 1; num < den; num++) {
                    leaves.add(new Fraction(whole * den + num, den));
                }
            }
        }

        Set<String> uniqueCanonicals = new HashSet<>();
        List<Expr> problems = new ArrayList<>();
        Random rand = new Random();
        int attempts = 0;
        final int MAX_ATTEMPTS = 1000000; // For 10k, safe

        while (problems.size() < n && attempts < MAX_ATTEMPTS) {
            attempts++;
            Expr expr = generateExpr(3, leaves, rand);
            if (expr.isValid()) {
                String can = expr.getCanonical();
                if (!uniqueCanonicals.contains(can)) {
                    uniqueCanonicals.add(can);
                    problems.add(expr);
                }
            }
        }

        if (problems.size() < n) {
            System.err.println("Warning: Generated only " + problems.size() + " unique problems (target: " + n + ").");
        }

        try (PrintWriter exerWriter = new PrintWriter(new FileWriter("Exercises.txt"));
             PrintWriter ansWriter = new PrintWriter(new FileWriter("Answers.txt"))) {
            for (int i = 0; i < problems.size(); i++) {
                Expr e = problems.get(i);
                exerWriter.println((i + 1) + ". " + e.toInfix() + " =");
                ansWriter.println((i + 1) + ". " + e.eval().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Generated " + problems.size() + " problems to Exercises.txt and Answers.txt");
    }

    private static Expr generateExpr(int rem, List<Fraction> leaves, Random rand) {
        if (rem == 0 || rand.nextDouble() < 0.4) {
            return new Leaf(leaves.get(rand.nextInt(leaves.size())));
        }
        String op = OPS[rand.nextInt(OPS.length)];
        int leftRem = rand.nextInt(rem);
        int rightRem = rem - 1 - leftRem;
        Expr left = generateExpr(leftRem, leaves, rand);
        Expr right = generateExpr(rightRem, leaves, rand);
        return new Binary(op, left, right);
    }

    static void grade(String exerFile, String ansFile) {
        try {
            List<String> exerLines = Files.readAllLines(Paths.get(exerFile));
            List<String> ansLines = Files.readAllLines(Paths.get(ansFile));
            List<Integer> correct = new ArrayList<>();
            List<Integer> wrong = new ArrayList<>();

            for (int i = 0; i < Math.min(exerLines.size(), ansLines.size()); i++) {
                String exerLine = exerLines.get(i).trim();
                String ansLine = ansLines.get(i).trim();

                // Parse exercise: "1. expr ="
                String[] exerParts = exerLine.split("\\.", 2);
                if (exerParts.length < 2) continue;
                String[] exprSplit = exerParts[1].trim().split("=", 2);
                if (exprSplit.length < 1) continue;
                String exprStr = exprSplit[0].trim();

                // Parse answer: "1. ans"
                String[] ansParts = ansLine.split("\\.", 2);
                if (ansParts.length < 2) continue;
                String ansStr = ansParts[1].trim();

                Fraction expected = Fraction.parse(ansStr);
                try {
                    Parser parser = new Parser(exprStr);
                    Expr expr = parser.parse();
                    Fraction computed = expr.eval();
                    if (computed.equals(expected)) {
                        correct.add(i + 1);
                    } else {
                        wrong.add(i + 1);
                    }
                } catch (Exception e) {
                    wrong.add(i + 1);
                }
            }

            try (PrintWriter gradeWriter = new PrintWriter(new FileWriter("Grade.txt"))) {
                String correctStr = correct.isEmpty() ? "" : correct.stream().map(String::valueOf).reduce((a, b) -> a + ", " + b).get();
                String wrongStr = wrong.isEmpty() ? "" : wrong.stream().map(String::valueOf).reduce((a, b) -> a + ", " + b).get();
                gradeWriter.println("Correct: " + correct.size() + " (" + correctStr + ")");
                gradeWriter.println("Wrong: " + wrong.size() + " (" + wrongStr + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner Parser class
    static class Parser {
        private String s;
        private int pos;

        public Parser(String input) {
            this.s = input;
            this.pos = 0;
        }

        public Expr parse() {
            return parseAdd();
        }

        private Expr parseAdd() {
            Expr e = parseMul();
            while (true) {
                String op = peekNextOp();
                if (op == null || (!op.equals("+") && !op.equals("-"))) break;
                consumeOp(op);
                Expr right = parseMul();
                e = new Binary(op, e, right);
            }
            return e;
        }

        private Expr parseMul() {
            Expr e = parseAtom();
            while (true) {
                String op = peekNextOp();
                if (op == null || (!op.equals("*") && !op.equals("/"))) break;
                consumeOp(op);
                Expr right = parseMul();
                e = new Binary(op, e, right);
            }
            return e;
        }

        private Expr parseAtom() {
            skipSpaces();
            if (pos >= s.length()) throw new RuntimeException("Unexpected end");
            if (s.charAt(pos) == '(') {
                pos++; // consume (
                Expr e = parseAdd();
                skipSpaces();
                if (pos < s.length() && s.charAt(pos) == ')') {
                    pos++; // consume )
                } else {
                    throw new RuntimeException("Missing )");
                }
                return e;
            } else {
                String numStr = parseNumberStr();
                return new Leaf(Fraction.parse(numStr));
            }
        }

        private String peekNextOp() {
            skipSpaces();
            if (pos >= s.length()) return null;
            char ch = s.charAt(pos);
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                return String.valueOf(ch);
            }
            return null;
        }

        private void consumeOp(String op) {
            skipSpaces();
            if (pos < s.length() && s.charAt(pos) == op.charAt(0)) {
                pos++;
            } else {
                throw new RuntimeException("Expected op: " + op);
            }
        }

        private void skipSpaces() {
            while (pos < s.length() && s.charAt(pos) == ' ') pos++;
        }

        private String parseNumberStr() {
            skipSpaces();
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char ch = s.charAt(pos);
                if (ch == ' ' || ch == ')' || (ch == '+' || ch == '-' || ch == '*' || ch == '/')) {
                    break;
                }
                sb.append(ch);
                pos++;
            }
            if (sb.length() == 0) throw new RuntimeException("Expected number");
            return sb.toString();
        }
    }
}