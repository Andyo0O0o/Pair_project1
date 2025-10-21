package com.example;

/**
 * 程序入口，负责解析命令行参数并启动题目生成。
 * 使用简单的手动解析（不依赖外部库如Picocli），验证 -n 和 -r 参数。
 */
public class Main {
    public static void main(String[] args) {
        try {
            // 检查参数长度
            if (args.length < 4) {
                printHelp();
                System.exit(1);
            }

            // 解析 -n 和 -r 参数
            int numQuestions = -1;
            int range = -1;
            for (int i = 0; i < args.length; i += 2) {
                if (args[i].equals("-n")) {
                    numQuestions = Integer.parseInt(args[i + 1]);
                } else if (args[i].equals("-r")) {
                    range = Integer.parseInt(args[i + 1]);
                } else {
                    printHelp();
                    System.exit(1);
                }
            }

            // 验证参数有效性
            if (numQuestions <= 0 || range <= 0) {
                System.out.println("Error: -n and -r must be positive integers");
                printHelp();
                System.exit(1);
            }

            // 启动题目生成
            ExpressionGenerator generator = new ExpressionGenerator(numQuestions, range);
            generator.generateAndSave();

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format for -n or -r");
            printHelp();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            printHelp();
            System.exit(1);
        }
    }

    /**
     * 打印帮助信息，当参数无效时调用。
     */
    private static void printHelp() {
        System.out.println("Usage: java Main -n <number> -r <range>");
        System.out.println("  -n: Number of questions to generate (positive integer)");
        System.out.println("  -r: Range of numbers and denominators (positive integer)");
    }
}