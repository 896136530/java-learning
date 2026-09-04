import java.io.*;
import java.util.*;

public class Day17_Ex5 {
    public static void main(String[] args) {
        // 题 5：复制文件（记得先建 src.txt，写一行 copy me!）
        CopyFile.copy("src.txt", "dst.txt");
        System.out.println("复制完成");   // 期望：复制完成
    }
}

// ===== 你的代码写在这里（类 CopyFile：static void copy(String src, String dst)）=====
class CopyFile{
    public static void copy(String src,String dst){
        List<String>allLines=new ArrayList<>();
        try(BufferedReader br=new BufferedReader(new FileReader( src))){
            String line;
            while((line=br.readLine())!=null){
                allLines.add(line);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        try(BufferedWriter bw=new BufferedWriter(new FileWriter(dst,false))){
            for(String line:allLines){
                bw.write(line);
                bw.newLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
// ===========================================