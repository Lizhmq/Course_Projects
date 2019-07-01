#include <cstdlib>
#include <cstdio>
#include <string.h>
#include <vector>
#include <functional>
#include <algorithm>
// #include "sp.h"
#include "dij.h"
// #include "fib.h"
using namespace std;

#define MODUL ((long long) 1 << 62)

extern double timer();            // in timer.cc: tells time use
extern int parse_gr( long *n_ad, long *m_ad, Node **nodes_ad, Arc **arcs_ad, 
		  long *node_min_ad, char *problem_name );
extern int parse_ss(long *sN_ad, long **source_array, char *aName);

struct Dij {
	long cnodes;
	Node *nodes;
	
};

int main(int argc, char **argv)
{
	double tm = 0.0;
	Arc *arcs;
	Node *nodes, *source = NULL;
	long n, m, nmin, nQ;
	long *source_array = NULL;
	DIJ *dij = NULL;
	
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

	// ArcLen(n, nodes, &minArcLen, &maxArcLen);      // other useful stats
	

	dij = new DIJ(n, nodes);
	dij->init();


	dist = 0;
	
	tm = timer();          // start timing
	for (int i = 0; i < nQ; i++) {
		source = nodes + source_array[i] - 1;
		dij->initS(source);
		dij->sp(source);
#ifdef CHECKSUM
		dist = source->dist;
		for ( node = nodes; node < nodes + n; node++ )
			// if (node->tStamp == sp->curTime) {
			dist = (dist + (node->dist % MODUL)) % MODUL;
		// }
	       fprintf(oFile,"d %lld\n", dist);
#endif
	}
	tm = (timer() - tm);   // finish timing

#ifndef CHECKSUM
	// now print the sp-specific stats
	dij->PrintStats(nQ);
	/* *round* the time to the nearest .01 of ms */
	fprintf(stderr,"c Time (ave, ms): %18.2f\n", 
		1000.0 * tm/(float) nQ);

	fprintf(oFile, "g %ld %ld %lld %lld\n", 
		n, m, minArcLen, maxArcLen);
	fprintf(oFile, "t %f\n", 1000.0 * tm/(float) nQ);
	fprintf(oFile, "v %f\n", (float) dij->cScans/ (float) nQ);
	fprintf(oFile, "i %f\n", (float) dij->cUpdates/ (float) nQ);
#endif

	dij->~DIJ();
	free(source_array);
	fclose(oFile);
	
	return 0;
}
