/* nodearc.h  by Andrew Goldberg, started 5/25/01
 *     Contains the definition of the node and arc class needed
 *     for all the code.
 */

#ifndef NODEARC_H
#define NODEARC_H

#define IN_NONE       0   // possible data structures the node can be in
#define IN_HEAP       1
#define IN_F          2
#define IN_BUCKETS    4
#define IN_SCANNED    5

typedef struct Node;      // so the arc knows about it
   
typedef struct Arc {
  long long len;         // arc length; long long may be an overkill
  struct Node *head;     // where the arc ends up
} Arc;

typedef struct Node {
  long long dist;        // tentative shortest path length to some node
  Arc   *first;          // first outgoing arc
  struct Node *parent;   // parent on the heap
  char where;   // what data structure we're in:  IN_* (above)
  unsigned int tStamp;

  // void *fibhp;   // for fibonacci heap

  struct {
    struct Node *next;          // next in bucket
    struct Node *prev;          // prev in bucket
    void *bucket;               // you should cast this
#ifndef MLB
    long long caliber;          // minimum incoming arc length
#endif
  } sBckInfo;                   // FOR SMART BUCKETS
} Node;


#endif
