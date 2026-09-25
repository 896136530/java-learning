import java.sql.*;
import java.util.*;
import java.lang.reflect.Proxy; 
public class Day28_Ex5 {
    public static void main(String[] args) throws Exception {
        // 题 5：为什么真实项目不用 DriverManager 每回新建连接？（跑一跑就懂了）
        try (Connection prepare = Db.getConnection()) {
            Setup.init(prepare);
        }

        int times = 30;

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            try (Connection c = Db.newConnection()) {
                countOf(c);
            }
        }
        long newCost = System.currentTimeMillis() - t1;

        MiniPool pool = new MiniPool(1);
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            try (Connection c = pool.getConnection()) {
                countOf(c);
            }
        }
        long poolCost = System.currentTimeMillis() - t2;

        System.out.println(times + " 次操作 —— 每次新建连接：" + newCost + " ms");
        System.out.println(times + " 次操作 —— 从池子借+还：" + poolCost + " ms");

        System.out.println("--- 池子里的连接会被复用吗 ---");
        MiniPool pool2 = new MiniPool(2);
        try (Connection a = pool2.getConnection();
             Connection b = pool2.getConnection()) {
            System.out.println("借两次是不是同一个对象：" + (a == b));
            a.close();
            try (Connection c = pool2.getConnection()) {
                System.out.println("还回去再借，是不是同一个对象：" + (c == a));
            }
        }
        // 期望（已真机验证，数字随机器变）：
        //   30 次操作 —— 每次新建连接：183 ms      ← 含 30 次 TCP + 认证
        //   30 次操作 —— 从池子借+还：53 ms        ← 连接早就连好了，只是搬对象
        //   --- 池子里的连接会被复用吗 ---
        //   借两次是不是同一个对象：false          ← 池子里有 2 个真连接，各借各的
        //   还回去再借，是不是同一个对象：false    ← ⚠️ 这个 false 也是**正常的**！
        //
        //   💡 为什么两个 false 反而说明"复用了"？
        //      因为借给你的是**每次现做的壳（代理）**，不是真连接本身；
        //      真连接留在池子里循环用，壳每借一次新建一个 → 壳与壳永不 ==。
        //      **"复用"的证据在 Ex4 的那些行**：借完 3 个池子剩 0 → 还 1 个剩 1 → 再借剩 0，
        //      全程只有构造池子时建的 3 个连接，一个都没多建（这就是池子的全部价值）。
    }

    static int countOf(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM student")) {
            rs.next();
            return rs.getInt(1);
        }
    }
}

// ===== 你的代码写在这里：class MiniPool =====

// ⚠️ 这题不用你写——把 Ex4 写好的 **MiniPool** 整个复制到这里即可
//    （复制 class MiniPool { ... } 那一整块，含它的构造器和三个方法）
//
// 复制的目的不是偷懒，而是让你体会一件事：
//    **连接池是个"通用零件"**，谁的代码都能拿去用；Ex4 里它是主角，Ex5 里它只是个"工具人"。
//    真实项目里这个零件叫 HikariCP：你不用自己写 MiniPool，只要 new HikariDataSource(config) 就行。
//
// 跑完看两个数字，理解为什么连接池是"必需品"：
//    · 每次 newConnection() 都要走一次 TCP 连接 + 握手 + 认证（几十毫秒级别）
//    · 池子里的连接是**已连好的**，借出来直接能用（微秒级别）
//    1000 个请求 × 每次新建连接 = 几十秒都在"建连接"上；用池子 → 忽略不计。
class MiniPool{
    private final List<Connection> idel=new ArrayList<>();
    private final int size;
    MiniPool(int size)throws SQLException{
        this.size=size;
        for(int i=0;i<size;i++){
            idel.add(Db.newConnection());
        }
    }
    public Connection getConnection(){
        if(idel.isEmpty()){
            return null;
        }
        Connection real=idel.remove(idel.size()-1); 
        return (Connection)  Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class<?>[]{Connection.class},
            (proxy,method,args)->{
                if(method.getName().equals("close")){
                    this.close(real);
                    return null;
                }
                return method.invoke(real,args);
            }
        );
    }
    public int size(){
        return idel.size();
    }
    public void close(Connection real){
        idel.add(real);
    }
}

// ===========================