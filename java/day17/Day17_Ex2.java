import java.io.*;

public class Day17_Ex2 {
    public static void main(String[] args) {
        // 题 2：数文件行数（记得先建 lines.txt，写 3 行）
        System.out.println("行数：" + LineCount.countLines("lines.txt")); // 期望：行数：3
    }
}

// ===== 你的代码写在这里（类 LineCount：static int countLines(String path)）=====
class LineCount {
    public static int countLines(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int count=0;
            String line;
            while((line=br.readLine())!=null){
                count++;
            }
            return count;
        }catch(IOException e){
            e.printStackTrace();
        }
            return -1;
        }
    }


// ===========================================