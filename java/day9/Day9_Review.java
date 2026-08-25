public class Day9_Review {
    public static void main(String[] args) {
        // ======== 接口 + 抽象类完整示例：USB 世界 ========
        // 1. 接口多态：USB 引用可以装任何"支持 USB 充电"的设备
        USB u1 = new Phone("小米手机");
        USB u2 = new Camera("单反相机");
        u1.charge(); // 小米手机正在充电
        u2.charge(); // 单反相机正在充电
        System.out.println("---- 抽象类多态 ----");
        Device d = new Phone("苹果手机");
        d.powerOn(); // 苹果手机开机了（普通方法）
        d.work();    // 苹果手机接打电话（抽象方法实现）
        System.out.println("---- 既是设备也是 USB ----");
        Phone p = new Phone("华为手机");
        p.charge();
        p.powerOn();
        p.work();
    }
}

// ---------- 接口：USB 标准（合同：必须能充电）----------
interface USB {
    void charge(); // 自动是 public abstract
}

// ---------- 抽象类：设备（半成品：开机写好了，怎么工作留给你）----------
abstract class Device {
    String name;

    Device(String name) {
        this.name = name;
    }

    void powerOn() {
        System.out.println(name + "开机了");
    }

    abstract void work(); // 抽象方法：怎么工作，子类自己实现
}

// ---------- 手机：既是设备，也支持 USB ----------
class Phone extends Device implements USB {
    Phone(String name) {
        super(name);
    }

    @Override
    void work() {
        System.out.println(name + "接打电话");
    }

    @Override
    public void charge() {
        System.out.println(name + "正在充电");
    }
}

// ---------- 相机：同样支持 USB ----------
class Camera extends Device implements USB {
    Camera(String name) {
        super(name);
    }

    @Override
    void work() {
        System.out.println(name + "拍照片");
    }

    @Override
    public void charge() {
        System.out.println(name + "正在充电");
    }
}
// ===========================================