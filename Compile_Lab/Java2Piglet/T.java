
class T {
    public static void main(String []args) {
        A a;
        B b;
        b = new B();
        a = b;
        System.out.println(a.assignA(10));
        System.out.println(b.assignB(20));
        System.out.println(a.print(a.print(1)));
        // System.out.println(b.print());
    }
}


class A {
    int age;
    public int assignA(int value) {
        age = value;
        return value;
    }
    public int print(int a) {
        System.out.println(a);
        return age;
    }
}

class B extends A {
    int age;
    public int assignB(int value) {
        age = value;
        return value;
    }
    // public int print() {
    //     System.out.println(age);
    //     return age;
    // }
}