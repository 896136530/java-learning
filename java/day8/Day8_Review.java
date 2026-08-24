public class Day8_Review {
    public static void main(String[] args) {
        // ======== 多态完整示例：一个音乐厅 ========
        // 1. 向上转型：父类引用指向子类对象
        Music m1 = new Guitar("木吉他");
        Music m2 = new Piano("三角钢琴");

        // 2. 动态绑定：同一个 play()，不同表现（编译看左边，运行看右边）
        m1.play(); // 吉他弹奏：木吉他
        m2.play(); // 钢琴弹奏：三角钢琴

        // 3. 多态参数：一个方法通吃所有乐器
        concert(m1);
        concert(m2);

        // 4. instanceof + 向下转型：调用子类独有的方法
        if (m1 instanceof Guitar) {
            Guitar g = (Guitar) m1;
            g.strum(); // 木吉他还能扫弦！
        }
    }

    // 参数用父类 Music：装谁就是谁
    static void concert(Music music) {
        music.play();
    }
}

// ---------- 父类（乐器）----------
class Music {
    String name;

    Music(String name) {
        this.name = name;
    }

    void play() {
        System.out.println("乐器演奏：" + name);
    }
}

// ---------- 子类 1（吉他）----------
class Guitar extends Music {
    Guitar(String name) {
        super(name);
    }

    @Override
    void play() {
        System.out.println("吉他弹奏：" + name);
    }

    void strum() { // 吉他独有方法
        System.out.println(name + "还能扫弦！");
    }
}

// ---------- 子类 2（钢琴）----------
class Piano extends Music {
    Piano(String name) {
        super(name);
    }

    @Override
    void play() {
        System.out.println("钢琴弹奏：" + name);
    }
}
// ===========================================