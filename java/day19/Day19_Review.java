import java.util.*;

public class Day19_Review {
    // ======== 实战全家桶：分段求和 + 银行取款 + 线程安全列表 ========

    static int total = 0;

    static synchronized void merge(int v) {
        total += v;
    }

    static class SumWorker implements Runnable {
        private int start, end;

        SumWorker(int s, int e) {
            start = s;
            end = e;
        }

        public void run() {
            int s = 0;
            for (int i = start; i <= end; i++) {
                s += i;
            }
            merge(s);
        }
    }

    static class Account {
        private int balance;

        Account(int b) {
            balance = b;
        }

        synchronized void withdraw(int m) {
            if (balance >= m) {
                balance -= m;
            }
        }

        int getBalance() {
            return balance;
        }
    }

    static class Person implements Runnable {
        private Account acc;

        Person(Account a) {
            acc = a;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                acc.withdraw(1000);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 1) 分段求和
        Thread[] ws = new Thread[4];
        for (int i = 0; i < 4; i++) {
            ws[i] = new Thread(new SumWorker(i * 25 + 1, (i + 1) * 25));
            ws[i].start();
        }
        for (Thread t : ws) {
            t.join();
        }
        System.out.println("总和：" + total);

        // 2) 银行取款
        Account acc = new Account(10000);
        Thread p1 = new Thread(new Person(acc));
        Thread p2 = new Thread(new Person(acc));
        p1.start();
        p2.start();
        p1.join();
        p2.join();
        System.out.println("余额：" + acc.getBalance());

        // 3) 线程安全列表
        final List<String> list = Collections.synchronizedList(new ArrayList<>());
        Thread a = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 5000; i++) {
                    list.add("x");
                }
            }
        });
        Thread b = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 5000; i++) {
                    list.add("x");
                }
            }
        });
        a.start();
        b.start();
        a.join();
        b.join();
        System.out.println("列表大小：" + list.size());
    }
}
// ===========================================