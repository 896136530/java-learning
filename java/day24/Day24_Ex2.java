import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Day24_Ex2 {
    public static void main(String[] args) throws Exception {
        // 题 2：把文件读回来（读取 + 逐行解析）
        // （main 里先用 NIO 造一个测试文件，你只负责写 ScoreReader.read）
        Files.write(Paths.get("scores2.txt"), Arrays.asList("张三,88", "李四,95", "王五,76"), StandardCharsets.UTF_8);

        List<String> lines = ScoreReader.read("scores2.txt");
        System.out.println("共读到 " + lines.size() + " 行");
        for (String s : lines) {
            System.out.println(s);
        }
        String[] parts = lines.get(0).split(",");
        System.out.println("第一行解析：" + parts[0] + " 考了 " + parts[1] + " 分");
        // 期望输出：
        //   共读到 3 行
        //   张三,88
        //   李四,95
        //   王五,76
        //   第一行解析：张三 考了 88 分
    }
}

// ===== 你的代码写在这里：class ScoreReader =====

// TODO：static List<String> read(String path)
//   · BufferedReader + readLine() 逐行读；**读到 null 就是文件末尾**（不是空字符串！）
//   · 每读一行 add 进 List，最后 return
//   · 编码指定 UTF-8：new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)
// ===========================================
