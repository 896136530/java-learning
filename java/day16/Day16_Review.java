public class Day16_Review {
    public static void main(String[] args) {
        // ======== 泛型全家桶 ========
        // 泛型盒
        Box<String> box = new Box<>();
        box.set("你好");
        System.out.println("泛型盒：" + box.get());

        // 双泛型对
        Pair<String, Integer> p = new Pair<>();
        p.set("小明", 18);
        System.out.println("对：" + p.getKey() + "-" + p.getValue());

        // 泛型方法 swap + 遍历
        Integer[] nums = {1, 2, 3};
        Swapper.swap(nums, 0, 2);
        System.out.print("交换后：");
        for (int i = 0; i < nums.length; i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(nums[i]);
        }
        System.out.println();

        // 泛型方法 printAll
        String[] pets = {"猫", "狗", "鸟"};
        Printer.printAll(pets);
        System.out.println("遍历：猫 狗 鸟");
    }
}

class Box<T> {
    private T value;
    public void set(T v) { this.value = v; }
    public T get() { return value; }
}

class Pair<K, V> {
    private K key;
    private V value;
    public void set(K k, V v) { this.key = k; this.value = v; }
    public K getKey() { return key; }
    public V getValue() { return value; }
}

class Swapper {
    public static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

class Printer {
    public static <T> void printAll(T[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(arr[i]);
        }
        System.out.println();
    }
}
// ===========================================