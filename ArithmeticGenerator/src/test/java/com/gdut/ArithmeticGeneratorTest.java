package com.gdut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ArithmeticGeneratorTest {
    @TempDir Path tempDir;  // 临时目录

    @Test
    void testGenerateBasic() throws IOException {  // 用例8: 生成小n
        // 先备份原文件（可选，手动删Exercises.txt）
        ArithmeticGenerator.generate(2, 3);  // 调用生成
        List<String> exercises = Files.readAllLines(Path.of("Exercises.txt"));
        assertEquals(2, exercises.size());  // 2题
        assertTrue(exercises.get(0).matches("\\d+\\. .* ="));  // 格式检查
    }

    @Test
    void testGradeCorrectWrong() throws IOException {  // 用例9: 批改
        Path exer = tempDir.resolve("exer.txt");
        Files.writeString(exer, "1. 1 + 1 =\n2. 1 + 2 =");  // 临时文件
        Path ans = tempDir.resolve("ans.txt");
        Files.writeString(ans, "1. 2\n2. 4");  // 第二题错（预期3）
        ArithmeticGenerator.grade(exer.toString(), ans.toString());
        List<String> grade = Files.readAllLines(Path.of("Grade.txt"));
        assertTrue(grade.get(0).contains("Correct: 1 (1)"));  // 1对
        assertTrue(grade.get(1).contains("Wrong: 1 (2)"));
    }

    @Test
    void testNoRParameter() {  // 用例10: 无-r参数错误
        // 模拟main无-r，检查System.err（简化：直接跑main看控制台）
        // 或改main为public static void runWithArgs(String[] args)
        // 预期: 打印错误信息
        assertThrows(IllegalArgumentException.class, () -> {
            // 如果你加了try-catch，测试抛异常
        });
    }
}