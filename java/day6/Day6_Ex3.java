public class Day6_Ex3 {
    public static void main(String[] args) {
        // 题 3：BankAccount 封装版（Day5 升级）——存/取款都带校验
        BankAccount acc = new BankAccount(1000);
        acc.deposit(500);
        acc.withdraw(200);
        acc.deposit(-1);        // 期望：存款金额不能为负
        acc.withdraw(99999);    // 期望：余额不足
        System.out.println("余额：" + acc.getBalance());  // 期望：余额：1300.0
    }
}

// ===== 你的代码写在这里（BankAccount 类：private balance + 构造方法 + deposit/withdraw 校验 + getBalance）=====


// ===========================================