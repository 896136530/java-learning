import java.io.*;

public class Day17_Ex4 {
    public static void main(String[] args) {
        // 题 4：追加一行（记得先建 notes.txt，写两行 a 和 b）
        AppendLine.appendLine("notes.txt", "c");
        System.out.println("追加完成");   // 期望：追加完成
    }
}

// ===== 你的代码写在这里（类 AppendLine：static void appendLine(String path, String line)）=====

// ===========================================