#include <linux/init.h>
#include <linux/printk.h>
#include <linux/kernel.h>
#include <linux/module.h>

extern char boot_command_line[];
extern char *saved_command_line;

int my_strlen(char *s)
{
  int i = 0;
  while (s[i++]);
  return i - 1;
}

static int __init dump_init(void)
{
	print_hex_dump(KERN_INFO, "boot_command_line: ", DUMP_PREFIX_NONE, 16, 1, boot_command_line, my_strlen(boot_command_line), 1);
	print_hex_dump(KERN_INFO, "saved_command_line: ", DUMP_PREFIX_NONE, 16, 1, saved_command_line, my_strlen(saved_command_line), 1);
	return 0;
}

static void __exit dump_exit(void)
{
	return;
}

module_init(dump_init);
module_exit(dump_exit);

