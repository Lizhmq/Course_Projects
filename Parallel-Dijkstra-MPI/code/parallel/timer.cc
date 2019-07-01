#include <sys/time.h>     
#include <sys/resource.h>
#include <unistd.h>

/*********************************************************************/
/*                                                                   */
/* USER time spent by this process in seconds.                       */
/* difference between two calls is processor time spent by your code */
/* needs: <sys/time.h>, <sys/resource.h>, <unistd.h>                 */
/* depends on compiler and OS                                        */
/*                                                                   */
/*********************************************************************/


double timer()
{
  struct rusage r;

  getrusage(0, &r);
  return (double)(r.ru_utime.tv_sec+r.ru_utime.tv_usec/(double)1000000);
}
