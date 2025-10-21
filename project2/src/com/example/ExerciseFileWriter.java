package com.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * 处理文件输出，批量写入 Exercises.txt 和 Answers.txt。
 * 优化为一次性写入，减少IO操作。
 */
public class ExerciseFileWriter {
    /**
     * 将字符串列表写入文件。
     * @param fileName 文件名
     * @param lines 要写入的行
     */
    public void writeToFile(String fileName, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to " + fileName + ": " + e.getMessage());
        }
    }
}