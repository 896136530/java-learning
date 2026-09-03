import java.io.*;

public class Day17_Review {
    public static void main(String[] args) throws IOException {
        // ======== IO 演练场：写 → 读 → 数行 → 删 ========
        String file = "io_demo.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("line1");
            bw.newLine();
            bw.write("line2");
            bw.newLine();
        }
        System.out.println("写入完成");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            System.out.println("读取第一行：" + br.readLine());
        }

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.readLine() != null) {
                count++;
            }
        }
        System.out.println("行数：" + count);

        new File(file).delete();
        System.out.println("文件已删除");
    }
}
// ===========================================