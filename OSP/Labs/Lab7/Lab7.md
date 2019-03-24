#### 1

编写模块fp，使用了浮点运算

```c
static int __init fp_init(void)
{
	float b = 1.1;
    printk("a: %d\n", a + (int)b);
    return 0;
}
```

**硬浮点**

编译选项中添加CFLAGS_fp.o=-mhard-float（为避免浮点部分被优化，设置-O0）

![c](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\c.PNG)

反汇编结果：

可以看到汇编中使用了flds, fstps等x87 FPU指令

![d](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\d.PNG)

**软浮点**

编译选项中添加CFLAGS_fp.o="-msoft-float"

![e](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\e.PNG)

make时有warning："__fixsfsi" undefined

![f](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\f.PNG)

反汇编结果：

原来应该出现浮点调用指令的地方变成了一个函数调用callq，由于没有链接调用地址未知，由warning提示这个函数调用应该是__fixsfsi，功能是将float转换为int。与编写的模块内容相符。

![g](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\g.PNG)

**分析**

从上面的结果可以看出，使用硬浮点编译的代码会使用FPU指令，需要机器有支持协处理器，而使用软浮点选项编译的程序会使用glibc的函数来模拟浮点指令，不需要FPU支持。

#### 2

**分解DO_INVALID_OP宏**

traps.c中定义了DO_ERROR宏来统一建立一些错误信号的错误处理函数，这里将do_invalid_op分离出来。

修改如图：

![a](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\a.PNG)

#### 3

编写普通程序：

![j](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\j.PNG)

编译生成a.out，反汇编查看main函数位置之后，在二进制指令中加上一个非法指令FF，再反汇编查看结果：

![h](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\h.PNG)

可以看到其中多出了一条指令：ff	(bad)

执行的结果是：

```txt
Hello world!
Illegal instruction (core dumped)
```

程序在执行printf之后，在ff指令处发生invalid op

修改do_error_trap函数：

在发生SIGILL时打印Never Giveup，由于trap之后回到原来指令，所以将ip加1，跳过这条指令，然后返回。

![b](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\b.PNG)

结果：

![k](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\k.PNG)

#### 4

编写模块task，想法是将已经遍历已经存在的用户进程，对某个用户的进程设置cpuaffinity；然后劫持系统调用sys_execve，对之后建立的进程设置cpuaffinity。

在include/linux/cpumask.h中查看cpumask接口，在kernel/sched/core.c中EXPORT_SYMBOL内核中的sched_setaffinity

![l](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\l.PNG)

模块内容如下：

![m](C:\Users\Joe\OneDrive - pku.edu.cn\Grade_3\OSP\Lab7\m.PNG)

编译之后insmod成功.