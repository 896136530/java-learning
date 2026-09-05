public class Day19_Ex2 {
    public static void main(String[] args) throws InterruptedException {
        // 题 2：银行取款——同一个账户，两个人各取 3 次 1000
        Account acc = new Account(10000);
        Thread p1 = new Thread(new Person(acc));
        Thread p2 = new Thread(new Person(acc));
        p1.start();
        p2.start();
        p1.join();
        p2.join();
        System.out.println("余额：" + acc.getBalance());   // 期望：余额：4000
    }
}

// ===== 你的代码写在这里（类 Account：私有余额+synchronized withdraw+getBalance；类 Person implements Runnable：run() 取 3 次每次 1000）=====

// ===========================================