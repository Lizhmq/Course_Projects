#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <map>
#include "dij.h"

using namespace std;

#define PROCESSNUM 4

DIJ::DIJ(long n, Node *nds)
{
	cNodes = n;
	nodes = nds;
}

void DIJ::initNode(Node *currentNode)
{
	// currentNode->where = IN_NONE;   // nodes not in any data structure yet
	currentNode->dist = VERY_FAR;   // not yet a shortest path
	currentNode->parent = NULL;
	// currentNode->fibhp = NULL;
	// currentNode->sBckInfo.bucket = NULL;
	// currentNode->sBckInfo.next = NULL;
	// currentNode->sBckInfo.prev = NULL;
	// currentNode->tStamp = curTime;	
}

void DIJ::init()
{
	Node *currentNode;
	curTime = 0;
	for (currentNode = nodes; currentNode < nodes + cNodes; currentNode++)
		initNode(currentNode);
}

DIJ::~DIJ()
{

}

void DIJ::initS(Node *source)
{
	Node *currentNode;
	for (currentNode = nodes; currentNode < nodes + cNodes; currentNode++)
		initNode(currentNode);
	// initNode(source);
	source->parent = source;
	source->dist = 0;               // all distances are to the source
}

void DIJ::sp(Node *source)
{
	cCalls++;

	Arc *a, *stopA;
	Node *curNode, *w;

	priority_queue<NodeState> *q = new priority_queue<NodeState>;			// using priority queue should change compare function of "NodeState"
	q->push(NodeState(0, source));
	while (!(q->empty())) {
		const NodeState &p = q->top();
		curNode = p.node;
		// if (curNode->dist != p.dist) {		// meaningless
		// 	q->pop();
		// 	continue;
		// }
		stopA = (curNode + 1)->first;
		for (a = curNode->first; a < stopA; ++a) {
			w = a->head;
			if (w->dist > curNode->dist + a->len) {
				w->dist = curNode->dist + a->len;
				q->push(NodeState(w->dist, w));
				cUpdates++;
			}
		}
		q->pop();
	}
}

// void DIJ::spfib(Node *source)
// {
// 	cCalls++;
// 	Arc *a, *stopA;
// 	Node *curNode, *w;
// 	FibonacciHeap<NodeState> *fibh = new FibonacciHeap<NodeState>();
// 	fibh->insert(NodeState(0, source));
// 	NodeState p;
// 	while (!(fibh->isEmpty())) {
// 		p = fibh->removeMinimum();
// 		curNode = p.node;
// 		stopA = (curNode + 1)->first;
// 		for (a = curNode->first; a < stopA; ++a) {
// 			w = a->head;
// 			if (w->dist > curNode->dist + a->len) {
// 				w->dist = curNode->dist + a->len;
// 				node<NodeState> *tmp = (node<NodeState> *)w->fibhp;
// 				if (tmp == NULL) {
// 					tmp = fibh->insert(NodeState(w->dist, w));
// 					w->fibhp = (void *)tmp;
// 					cInserts++;
// 				} else {
// 					fibh->decreaseKey(tmp, NodeState(w->dist, w));
// 				}
// 				cUpdates++;
// 			}
// 		}
		
// 	}
// 	delete fibh;
// }

void DIJ::spms(Node *source)
{
	cCalls++;
	
	Arc *a, *stopA;
	Node *curNode, *w;
	long long tmpdist;

	multimap<int, Node *> m;				// using multimap should change compare function of "NodeState"
	m.insert(make_pair(0, source));
	while (!m.empty()) {
		multimap<int, Node *>::iterator p = m.begin();
		curNode = p->second;
		stopA = (curNode + 1)->first;
		for (a = curNode->first; a < stopA; ++a) {
			w = a->head;
			if (w->dist > curNode->dist + a->len) {
				tmpdist = w->dist;
				w->dist = curNode->dist + a->len;
				pair<multimap<int, Node *>::iterator, multimap<int, Node *>::iterator> range = m.equal_range(tmpdist);
				multimap<int, Node *>::iterator i = range.first;
				for (; i != range.second; ++i) {
					if (i->second == w)
						break;
				}
				if (i != range.second)
					i->second->dist = w->dist;
				else {
					m.insert(make_pair(w->dist, w));
					cInserts++;
				}
				cUpdates++;
			}
		}
		m.erase(p);
	}
}

void DIJ::initStats()
{
	cScans = cUpdates = cInserts = 0;
}

void DIJ::PrintStats(long tries)
{
	fprintf(stderr, "c Inserts (ave): %20.1f     Updates (ave): %10.1f\n", 
	(float) cInserts / (float) tries, 
	(float) cUpdates / (float) tries);
}