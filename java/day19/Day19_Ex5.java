public class Day19_Ex5 {
    public static void main(String[] args) throws InterruptedException {
        // 题 5：大卖票——100 张票，6 个窗口同时卖，一张不多一张不少
        Thread[] ts = new Thread[6];
        for (int i = 0; i < 6; i++) {
            ts[i] = new Thread(new Seller());
            ts[i].start();
        }
        for (Thread t : ts) {
            t.join();
        }
        System.out.println("剩余票数：" + TicketBox.tickets);   // 期望：剩余票数：0
    }
}

// ===== 你的代码写在这里（类 TicketBox：static int tickets=100 + static synchronized boolean sell()；类 Seller implements Runnable：while(TicketBox.sell())）=====

// ===========================================