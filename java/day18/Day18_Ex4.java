public class Day18_Ex4 {
    public static void main(String[] args) throws InterruptedException {
        // 题 4：睡眠闹钟——打印 3 声叮咚，每声间隔 200 毫秒
        Alarm a = new Alarm();
        a.start();
        a.join();
        System.out.println("闹钟结束");
    }
}

// ===== 你的代码写在这里（类 Alarm extends Thread：run() 循环 3 次：打印「叮咚 i」+ Thread.sleep(200)）=====

// ===========================================