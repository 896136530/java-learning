import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Day24_Ex1 {
    public static void main(String[] args) throws Exception {
        // 题 1：把成绩单写成文件（写入入门）
        List<String> lines = Arrays.asList("张三,88", "李四,95", "王五,76");
        ScoreWriter.write("scores.txt", lines);

        File f = new File("scores.txt");
        System.out.println("已写入 " + lines.size() + " 行");
        System.out.println("文件存在：" + f.exists());
        System.out.println("文件非空：" + (f.length() > 0));
        // 期望输出：
        //   已写入 3 行
        //   文件存在：true
        //   文件非空：true
    }
}

// ===== 你的代码写在这里：class ScoreWriter =====

// TODO：static void write(String path, List<String> lines)
//   · 用 PrintWriter 把每一行 println 到 path（**覆盖**写，不是追加）
//   · 中文别乱码：new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))
//   · 要保证数据真的落盘 → 用 try-with-resources（自动 close）
class ScoreWriter{
    public static void write(String path,List<String> lines) throws Exception{
        try(PrintWriter pw=new PrintWriter(new OutputStreamWriter(new FileOutputStream(path),StandardCharsets.UTF_8))){
            
            for(String i : lines){
                pw.println(i);
            }
            pw.close();
}
}
}
// ===========================================
