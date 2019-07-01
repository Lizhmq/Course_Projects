#include <cstdlib>
#include <cstdio>
#include <string.h>
#include <vector>
#include <functional>
#include <algorithm>
#include <queue>
#include "mpi.h"

#include "values.h"
#include "nodearc.h"
#include "smartq.h"
#include "sp.h"
// #include "fib.h"
using namespace std;

// #define PROCESSNUM 4
#define MODUL ((long long) 1 << 62)

extern double timer();            // in timer.cc: tells time use
extern int parse_gr( long *n_ad, long *m_ad, Node **nodes_ad, Arc **arcs_ad, 
		  long *node_min_ad, char *problem_name );
extern int parse_ss(long *sN_ad, long **source_array, char *aName);


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
};


int main(int argc, char **argv)
{
	double tm = 0.0;
	Arc *arcs;
	Node *nodes, *source = NULL;
	long n, m, nmin, nQ;
	long *source_array = NULL;
	
	char gName[100], aName[100], oName[100];
	FILE *oFile;
	long long dist;
	double dDist;
	long long maxArcLen, minArcLen;

#if (defined CHECKSUM) && (!defined SINGLE_PAIR)
	Node *node;
#endif

	if (argc != 4) {
		fprintf(stderr, 
			"Usage: \"%s <graph file> <aux file> <out file>\"\n", argv[0]);
		exit(0);
	}

	strcpy(gName, argv[1]);
	strcpy(aName, argv[2]);
	strcpy(oName, argv[3]);
	oFile = fopen(oName, "a");

	fprintf(stderr, "c ---------------------------------------------------\n");
	fprintf(stderr, "c Dijkstra DIMACS Challenge version \n");
	fprintf(stderr, "c Copyright C by Lzzz., lizhmq@pku.edu.cn.\n");
	fprintf(stderr, "c ---------------------------------------------------\n");

	parse_gr(&n, &m, &nodes, &arcs, &nmin, gName ); 
	printf("p res ss sq\n");
	parse_ss(&nQ, &source_array, aName);
	
	fprintf(oFile, "f %s %s\n", gName, aName);
	fprintf(stderr,"c\n");

	ArcLen(n, nodes, &minArcLen, &maxArcLen);      // other useful stats
	
	dDist = maxArcLen * (double) (n-1);
	if (dDist > VERY_FAR) {
		fprintf(stderr, "Warning: distances may overflow\n");
		fprintf(stderr, "         proceed at your own risk!\n");
	}

	MPI_Init(&argc, &argv);
	int ps, rank;
	MPI_Comm_size(MPI_COMM_WORLD, &ps);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);

	fprintf(stderr,"c Nodes: %24ld       Arcs: %22ld\n",  n, m);
	fprintf(stderr,"c MinArcLen: %20lld       MaxArcLen: %17lld\n", 
		minArcLen, maxArcLen);
	fprintf(stderr,"c Trials: %23ld\n", nQ);


	// preprocessing

	long segnum = (n + ps - 1) / ps;
	Node *startNode = nodes + rank * segnum;
	Node *endNode = nodes + min((rank + 1) * segnum, n);

	long long *mindist = new long long[n];	// global !!!
	long long *minout = new long long[n];
	long long *minin = new long long[n];	// global !!!
	for (int i = 0; i < n; ++i) {
		minout[i] = maxArcLen + 1;
		minin[i] = maxArcLen + 1;
		mindist[i] = maxArcLen * (double) (n-1);
	}

	Node *tmpNode;
	Arc *tmpArc, *endArc;
	for (Node *tmp = startNode; tmp < endNode; ++tmp) {
		tmpArc = tmp->first;
		endArc = (tmp + 1)->first;
		while (tmpArc < endArc) {
			minout[tmp - startNode] = min(minout[tmp - startNode], tmpArc->len);
			// lock !!!
			minin[(tmpArc->head) - startNode] = min(minin[(tmpArc->head) - startNode], tmpArc->len);
		}
	}

	priority_queue<NodeState> q;
	priority_queue<NodeState> q_insimple;
	priority_queue<NodeState> q_outsimple;
	// reduce minimum from q
	
	// criteria = IN-OUT-SIMPLE CRITIRIA
	/*
	 * for v in criteria:
	 * 	for e in v.edges:
	 * 		relax(e)	update mindist(not accurate, so atomic isn't needed)
	 * 		update queues
	 * 		update local vertexs immediately
	 * 		buffer remote vertexs
	 * settle buffered vertexs
	 * 
	 * 
	*/

	dist = 0;
	
	// tm = timer();          // start timing
	for (int i = 0; i < nQ; i++) {
		source = nodes + source_array[i] - 1;
		solve(source);


#ifdef CHECKSUM
		dist = source->dist;
		for ( node = nodes; node < nodes + n; node++ )
			dist = (dist + (node->dist % MODUL)) % MODUL;
	       fprintf(oFile,"d %lld\n", dist);
#endif
	}
	// tm = (timer() - tm);   // finish timing




#ifndef CHECKSUM
	// now print the sp-specific stats
	

	// dij->PrintStats(nQ);
	
	
	/* *round* the time to the nearest .01 of ms */
	// fprintf(stderr,"c Time (ave, ms): %18.2f\n", 
	// 	1000.0 * tm/(float) nQ);

	fprintf(oFile, "g %ld %ld %lld %lld\n", 
		n, m, minArcLen, maxArcLen);
	// fprintf(oFile, "t %f\n", 1000.0 * tm/(float) nQ);
	// fprintf(oFile, "v %f\n", (float) dij->cScans/ (float) nQ);
	// fprintf(oFile, "i %f\n", (float) dij->cUpdates/ (float) nQ);
#endif

	free(source_array);
	fclose(oFile);
	
	return 0;
}