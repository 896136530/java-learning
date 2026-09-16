import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Day24_Ex4 {
    public static void main(String[] args) throws Exception {
        // 题 4：文件不存在也不能崩（异常处理）
        Files.write(Paths.get("scores4.txt"), Arrays.asList("张三,88", "李四,95", "王五,76"), StandardCharsets.UTF_8);

        List<String> none = SafeReader.read("no_such_file.txt");
        System.out.println("不存在文件读到 " + none.size() + " 行");

        List<String> ok = SafeReader.read("scores4.txt");
        System.out.println("存在文件读到 " + ok.size() + " 行");
        // 期望输出：
        //   [提示] 文件不存在：no_such_file.txt
        //   不存在文件读到 0 行
        //   存在文件读到 3 行
    }
}

// ===== 你的代码写在这里：class SafeReader =====

// TODO：static List<String> read(String path)（注意：**不抛异常**，方法签名里没有 throws）
//   · 文件不存在 → 打印 "[提示] 文件不存在：" + path，然后返回**空列表**（不是 null！）
//   · 其他读取出错 → 打印 "[提示] 读取失败：" + path
//   · 正常 → 返回所有行
//   · catch 顺序：FileNotFoundException（子类）写在 IOException（父类）前面
class SafeReader{
    public static List<String> read(String path){
        String line;
        List<String>lines=new ArrayList<>();
        try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),StandardCharsets.UTF_8))){
            while((line=br.readLine())!=null){
                lines.add(line);
            }
        }catch(FileNotFoundException e){
            System.out.println("[提示] 文件不存在：" + path);
        }catch(IOException e){
            System.out.println("[提示] 读取失败：" + path);
        }
        return lines;
    }
}

// ===========================================
