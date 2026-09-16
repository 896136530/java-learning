import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Day24_Ex3 {
    public static void main(String[] args) throws Exception {
        // 题 3：追加一行（append 模式）
        Files.write(Paths.get("scores3.txt"), Arrays.asList("张三,88", "李四,95"), StandardCharsets.UTF_8);

        AppendUtil.append("scores3.txt", "王五,76");

        List<String> lines = Files.readAllLines(Paths.get("scores3.txt"), StandardCharsets.UTF_8);
        System.out.println("最终 " + lines.size() + " 行");
        System.out.println(lines);
        // 期望输出：
        //   最终 3 行
        //   [张三,88, 李四,95, 王五,76]      ← 原来那两行必须还在（说明是追加不是覆盖）
    }
}

// ===== 你的代码写在这里：class AppendUtil =====

// TODO：static void append(String path, String line)
//   · 关键：追加模式 → new FileOutputStream(path, true)，那个 true 就是 append 开关
//   · 忘了写 true 的后果：新内容把旧内容**全部覆盖**，前两行消失
//   · 完整版：new PrintWriter(new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8))
class AppendUtil{
    public static void append(String path,String line) throws Exception{
        try(PrintWriter pw=new PrintWriter(new OutputStreamWriter(new FileOutputStream(path,true),StandardCharsets.UTF_8))){
            pw.println(line);
            pw.close();
        }
    }
}
// ===========================================
