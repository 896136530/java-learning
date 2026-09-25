package tool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 固定的工具类（**别改**）—— 教你"怎么在代码里读工程文件"。
 *
 * ⭐ 今天最重要的认知之一：
 *    Maven 项目里的文件有两类，读法完全不一样：
 *
 *    ① 【源代码目录里的文件】src/main/resources/xxx
 *       → 打包时会被塞进 jar 的根目录 → 用 **类路径** 读：getResourceAsStream("xxx")
 *       → ✅ 这种写法在 IDE 里能跑、在 jar 里也能跑（正确姿势）
 *
 *    ② 【磁盘上的任意文件】比如 C:\Users\...\abc.txt 或 "abc.txt"（相对当前目录）
 *       → 用 new FileInputStream("路径") 或 Files.readString(Path.of("路径"))
 *       → ⚠️ 一旦打成 jar，jar 里没有"当前目录"的概念，这种写法经常读不到
 *
 *    一句话记法：**跟着代码走的文件放 resources、用类路径读；用户数据文件用磁盘路径读。**
 */
public class Hold {

    private Hold() {
        // 工具类不需要被 new，把构造器藏起来（习惯，不是必须）
    }

    /**
     * 读取 resources 目录下的文本文件，返回全部内容（UTF-8）。
     * 例：readResource("db.properties") —— 不用写 src/main/resources/ 前缀！
     */
    public static String readResource(String name) {
        try (InputStream in = Hold.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalArgumentException("类路径里找不到：" + name + "（确认它在 src/main/resources/ 下）");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读文件失败：" + name, e);
        }
    }
}