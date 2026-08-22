public class Day6_Review {
    public static void main(String[] args) {
        // ======== 封装完整示例：一个"防呆"的账户 ========
        // 对比 Day5：属性全 public，谁都能乱改
        // 今天：private 藏起来 + 方法带校验，怎么折腾都坏不了

        Account acc = new Account("小明", 1000);

        acc.deposit(500);          // 正常存
        acc.deposit(-100);         // 非法存 → 被拦
        acc.withdraw(300);         // 正常取
        acc.withdraw(99999);       // 超额取 → 被拦
        acc.setBalance(-500);      // 想改成负数？setter 校验拦下

        acc.showInfo();
        // 输出：
        // 存款金额不能为负
        // 余额不足
        // 余额修改不合法：-500.0
        // 账户：小明，余额：1200.0
    }
}

// ---------- 封装版账户 ----------
class Account {
    private String owner;
    private double balance;

    Account(String owner, double balance) {
        this.owner = owner;
        this.balance = balance;
    }

    void deposit(double money) {
        if (money < 0) {
            System.out.println("存款金额不能为负");
            return;
        }
        balance += money;
    }

    void withdraw(double money) {
        if (money < 0) {
            System.out.println("取款金额不能为负");
        } else if (balance < money) {
            System.out.println("余额不足");
        } else {
            balance -= money;
        }
    }

    void setBalance(double balance) {
        if (balance < 0) {
            System.out.println("余额修改不合法：" + balance);
            return;
        }
        this.balance = balance;
    }

    void showInfo() {
        System.out.println("账户：" + owner + "，余额：" + balance);
    }
}
// ===========================================