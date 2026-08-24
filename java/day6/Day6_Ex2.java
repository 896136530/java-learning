public class Day6_Ex2 {
    public static void main(String[] args) {
        // 题 2：Person 年龄校验——非法年龄拒绝修改
        Person p = new Person();
        p.setAge(20);
        p.setAge(-5);    // 期望：年龄不合法：-5
        p.setAge(999);   // 期望：年龄不合法：999
        System.out.println("年龄：" + p.getAge());  // 期望：年龄：20（非法值没改成）
    }
}

// ===== 你的代码写在这里（Person 类：private 属性 + 带校验的 setAge + getAge）=====
class Person{
    private int age;
    public int getAge(){
return age;
    }
    public void   setAge(int age){
if(age<0||age>120){
    System.out.println("年龄不合法："+age);
    return;
}
this.age=age;
    }
}

// ===========================================