public class Day18_Ex5 {
    public static void main(String[] args) throws InterruptedException {
        // 题 5：卖票战——5 张票，两个窗口卖，一张不多一张不少
        Thread t1 = new Thread(new Seller());
        Thread t2 = new Thread(new Seller());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("剩余票数：" + TicketBox.tickets);   // 期望：剩余票数：0
    }
}

// ===== 你的代码写在这里（类 TicketBox：static int tickets=5 + static synchronized boolean sell()；类 Seller implements Runnable：while 卖票打印）=====

// ===========================================