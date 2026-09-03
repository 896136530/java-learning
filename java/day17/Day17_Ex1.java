import java.io.*;

public class Day17_Ex1 {
    public static void main(String[] args) {
        // 题 1：读文件第一行（记得先建 hello.txt，内容 Hello IO!）
        System.out.println("第一行：" + FileRead.readFirstLine("hello.txt"));  // 期望：第一行：Hello IO!
    }
}

// ===== 你的代码写在这里（类 FileRead：static String readFirstLine(String path)）=====

// ===========================================