#ifndef __DIJ_
#define __DIJ_


#include <stdlib.h>
#include <queue>
#include <vector>
#include <functional>
#include <algorithm>
#include "values.h"
#include "nodearc.h"
// #include "stack.h"
// #include "smartq.h"
using namespace std;

#define VERY_FAR            9223372036854775807LL // LLONG_MAX
#define FAR                 MAXLONG

// extern void ArcLen(long cNodes, Node *nodes,
// 		   long long *pMin = NULL, long long *pMax = NULL);

struct NodeState {
	long long dist;
	Node *node;
	NodeState(long long len, Node *n)
	{
		dist = len;
		node = n;
	}
	NodeState()
	{
		dist = 0;
		node = NULL;
	}
	friend	bool operator < (const NodeState &a, const NodeState &b)
	{
		return a.dist > b.dist || a.dist == b.dist && a.node > b.node;
	}
	// friend bool operator < (const NodeState &a, const NodeState &b)	// for fib heap
	// {
	// 	return a.dist < b.dist || a.dist == b.dist && a.node < b.node;
	// }
	// friend bool operator == (const NodeState &a, const NodeState &b)
	// {
	// 	return a.dist == b.dist && a.node == b.node;
	// }
	// friend bool operator > (const NodeState &a, const NodeState &b)
	// {
	// 	return b < a;
	// }
};

class DIJ {
private:
	long cNodes;
	Node *nodes;

public:
	DIJ(long n, Node *nds);
	~DIJ();
	void init();
	void initS(Node *source);
	void initNode(Node *source);
	void sp(Node *source);
	void spms(Node *source);
	// void spfib(Node *source);
	
	long cCalls;
	long long cScans;
	long long cUpdates;
	long long cInserts;
	unsigned int curTime;
	void PrintStats(long tries);
	void initStats();
};


#endif
