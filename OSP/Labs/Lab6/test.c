#include <linux/unistd.h>
#include <syscall.h>
#include <sys/types.h>
#include <stdio.h>
#include <unistd.h>
#include <syslog.h>

int main()
{
	unsigned int c0 = syscall(233);
	unsigned int c1 = c0;
	unsigned int c2;
	syslog(LOG_KERN, "Initial count: %d.\n", c0);
	while (1) {
		sleep(60);
		c2 = syscall(233);
		openlog("mkdir_count_log", LOG_KERN, 0);
		syslog(LOG_USER | LOG_DEBUG, 
			"%d sys_mkdir calls in last one minute. %d calls in all.\n", c2 - c1, c2 - c0);
		closelog();
		c1 = c2;
	}
	return 0;
}
