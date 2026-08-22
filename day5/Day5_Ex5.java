public class Day5_Ex5 {
    public static void main(String[] args) {
        // 题 5：银行账户——开户 1000 → 存 500 → 取 200 → 打印余额
        BankAccount acc = new BankAccount(1000);
        acc.deposit(500);
        acc.withdraw(200);
        System.out.println("余额：" + acc.getBalance()); // 期望 1300.0
    }
}

// ===== 你的代码写在这里（定义 BankAccount 类：balance 属性 + 构造方法 +
// deposit/withdraw/getBalance）=====
class BankAccount {
    int balance;

    BankAccount(int n) {
        balance = n;
    }

    void withdraw (int m) {
        if (balance > m) {
            balance = balance - m;
        } else {
            System.out.println("余额不足");
            return;
        }
    }

    void deposit (int n) {
        balance = balance + n;
    }

    double getBalance() {
        return balance ;
    }
}

// ===========================================