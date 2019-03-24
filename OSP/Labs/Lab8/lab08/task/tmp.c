#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/mutex.h>

DEFINE_MUTEX(dlock);

static int __init dlock_init(void)
{
	mutex_lock(&dlock);
	mutex_lock(&dlock);
	return 0;
}

static void __exit dlock_exit(void)
{
	return;
}

module_init(dlock_init);
module_exit(dlock_exit);
MODULE_LICENSE("GPL");

