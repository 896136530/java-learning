import java.util.Objects;

public class Day10_Review {
    public static void main(String[] args) {
        // ======== Object 全家桶完整示例：歌曲播放列表 ========
        Song s1 = new Song("晴天", "周杰伦");
        Song s2 = new Song("晴天", "周杰伦");
        System.out.println(s1);                              // 晴天 - 周杰伦（toString）
        System.out.println("两首歌相同？" + s1.equals(s2));    // true（equals 比内容）
        System.out.println("哈希相同？" + (s1.hashCode() == s2.hashCode())); // true
        System.out.println("运行时类型：" + s1.getClass().getName()); // Song
        Object o = s1;  // 万物皆 Object：任何对象都能放进 Object 引用
        System.out.println("放进 Object 后：" + o);           // 还是调 Song 的 toString（多态）
    }
}

// ---------- Song：重写三件套的标准模板 ----------
class Song {
    String title;
    String singer;

    Song(String title, String singer) {
        this.title = title;
        this.singer = singer;
    }

    @Override
    public String toString() {
        return title + " - " + singer;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Song) {
            Song s = (Song) o;
            return title.equals(s.title) && singer.equals(s.singer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, singer); // 标准写法：任意多个字段
    }
}
// ===========================================