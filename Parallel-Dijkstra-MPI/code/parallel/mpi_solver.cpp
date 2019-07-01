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
using namespace std;

#define MODUL ((long long) 1 << 62)
#define VERY_FAR 4557430888798830399LL // 0x3f3f3f3f3f3f3f3fLL

extern double timer();            // in timer.cc: tells time use
extern int parse_gr( long *n_ad, long *m_ad, Node **nodes_ad, Arc **arcs_ad, 
		  long *node_min_ad, char *problem_name);
extern int parse_ss(long *sN_ad, long **source_array, char *aName);


void Dijkstra_Init(Node *nodes, long long loc_dist[], int loc_known[],
                    int loc_n, int src);
void Dijkstra(Node *nodes, long long loc_dist[], int loc_n, int n,
              MPI_Comm comm, int src);
int Find_min_dist(long long loc_dist[], int loc_known[], int loc_n);
void reduce_gr(Node **nodes, Arc **arcs);

long segnum;
long start;
long loc_n;
int ps, my_rank;

class State {
public:
	long long dist;
	long loc_v;
	friend bool operator < (const State &a, const State &b) {
		return a.dist > b.dist || a.dist == b.dist && a.loc_v > b.loc_v;
	}
};
priority_queue<State> q;

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

#ifndef CHECKSUM
	fprintf(stderr, "c ---------------------------------------------------\n");
	fprintf(stderr, "c Dijkstra DIMACS Challenge version \n");
	fprintf(stderr, "c Copyright C by Lzzz., lizhmq@pku.edu.cn.\n");
	fprintf(stderr, "c ---------------------------------------------------\n");
#endif

	MPI_Init(&argc, &argv);
	MPI_Comm comm = MPI_COMM_WORLD;
	MPI_Comm_size(comm, &ps);
	MPI_Comm_rank(comm, &my_rank);



	if (my_rank == 0) {
		oFile = fopen(oName, "a");
	}

	parse_gr(&n, &m, &nodes, &arcs, &nmin, gName ); 
	parse_ss(&nQ, &source_array, aName);

	reduce_gr(&nodes, &arcs);
	long long *global_dist;
	long long *loc_dist;
	


	if (my_rank == 0)
		global_dist = (long long *)malloc(n * sizeof(long long));
	segnum = n / ps;		// floor
	start = my_rank * segnum;
	if (ps == my_rank + 1)
		loc_n = n - start;
	else
		loc_n = segnum;
	loc_dist = (long long *)malloc(loc_n * sizeof(long long));

	int *recvcnts = (int *)malloc(ps * sizeof(int));
	int *displs = (int *)malloc(ps * sizeof(int));

	for (int i = 0; i < ps - 1; ++i) {
		recvcnts[i] = segnum;
		displs[i] = i * segnum;
	}
	recvcnts[ps - 1] = n - (ps - 1) * segnum;
	displs[ps - 1] = (ps - 1) * segnum;
#ifndef CHECKSUM
	if (my_rank == 0)
		tm = timer();
#endif
	int src = 0;
	for (int i = 0; i < nQ; i++) {
		src = source_array[i] - 1;
		Dijkstra(nodes, loc_dist, loc_n, n, comm, src);
		MPI_Gatherv(loc_dist, loc_n, MPI_LONG_LONG, global_dist, recvcnts, displs, MPI_LONG_LONG, 0, comm);
		if (my_rank == 0) {
#ifdef CHECKSUM
		dist = 0;
		for (int i = 0; i < n; ++i) {
			// if (i < 20)
			// 	fprintf(stderr, "%d ", global_dist[i]);
			dist = (dist + global_dist[i]) % MODUL;
		}
		// fprintf(stderr, "\n");
		fprintf(oFile,"d %lld\n", dist);
#endif
		}
	}
	if (my_rank == 0) {
#ifndef CHECKSUM
		tm = (timer() - tm);
#endif
		free(global_dist);
	}
	free(recvcnts);
	free(displs);
	free(loc_dist);
	free(source_array);
	if (my_rank == 0) {
#ifndef CHECKSUM
	
	/* *round* the time to the nearest .01 of ms */
	if (my_rank == 0) {
		fprintf(stderr,"c Time (ave, ms): %18.2f\n", 
			1000.0 * tm/(float) nQ);
	}
#endif
		fclose(oFile);
	}
	MPI_Finalize();
	return 0;
}



void Dijkstra_Init(Node *nodes, long long loc_dist[], int loc_known[],
                    int loc_n, int src) {
	int loc_v;

	State cur;
	while (!q.empty())
		q.pop();
	for (loc_v = 0; loc_v < loc_n; loc_v++) {
		loc_known[loc_v] = 0;
		loc_dist[loc_v] = VERY_FAR;
		cur.dist = VERY_FAR;
		cur.loc_v = loc_v;
		q.push(cur);
	}
	if (src >= start && src < start + loc_n) {
		loc_known[src - start] = 1;
		loc_dist[src - start] = 0;
	}
	Node *tmp = nodes + src;
	Arc *a, *stopA = (tmp + 1)->first;
	for (a = tmp->first; a != stopA; ++a) {
		if ((a->head - nodes) >= start && (a->head - nodes) < start + loc_n) {
			loc_dist[a->head - nodes - start] = a->len;
			cur.dist = a->len;
			cur.loc_v = a->head - nodes - start;
			q.push(cur);
		}
	}
	
}


void Dijkstra(Node *nodes, long long loc_dist[], int loc_n, int n,
              MPI_Comm comm, int src) {

    int i, loc_v, loc_u, new_dist;
    long long dist_glbl_u;
    int glbl_u;
    int *loc_known;
    char loc_buf[8 + 4];
    char glb_buf[8 + 4];

    loc_known = (int *)malloc(loc_n * sizeof(int));

    Dijkstra_Init(nodes, loc_dist, loc_known, loc_n, src);
    /* Run loop n - 1 times since we already know the shortest path to global
       vertex 0 from global vertex 0 */
    for (i = 0; i < n - 1; i++) {
	
        loc_u = Find_min_dist(loc_dist, loc_known, loc_n);

	long long *p1 = (long long *)loc_buf;
	int *p2 = (int *)(loc_buf + 8);
        if (loc_u != -1) {
            *p1 = loc_dist[loc_u];
            *p2 = loc_u + start;
        }
        else {
            *p1 = VERY_FAR;
            *p2 = -1;
        }
        /* Get the minimum distance found by the processes and store that
           distance and the global vertex in glbl_min
        */
        MPI_Allreduce(loc_buf, glb_buf, 1, MPI_DOUBLE_INT, MPI_MINLOC, comm);

        dist_glbl_u = *(long long *)glb_buf;
        glbl_u = *(int *)(glb_buf + 8);

	if (*p2 == glbl_u)
		q.pop();

	// if (i < 20)
	// 	fprintf(stderr, "%d ", glbl_u);
	// fprintf(stderr, "\n");
        /* This test is to assure that loc_known is not accessed with -1 */
        if (glbl_u == -1)
            break;

        /* Check if global u belongs to process, and if so update loc_known */
        if (start <= glbl_u && glbl_u < start + loc_n) {
            loc_u = glbl_u - start;
            loc_known[loc_u] = 1;
        }

	State cur;
	Node *tmp = nodes + glbl_u;
	Arc *a, *stopA = (tmp + 1)->first;
	for (a = tmp->first; a != stopA; ++a) {
		if ((a->head - nodes) >= start && (a->head - nodes) < start + loc_n) {
			if (loc_dist[a->head - nodes - start] > a->len + dist_glbl_u) {
				loc_dist[a->head - nodes - start] = a->len + dist_glbl_u;
				cur.dist = a->len + dist_glbl_u;
				cur.loc_v = a->head - nodes - start;
				q.push(cur);
			}
		}
	}
    }
    free(loc_known);
}


int Find_min_dist(long long loc_dist[], int loc_known[], int loc_n) {
    long loc_u = -1, loc_v;
    long long shortest_dist = VERY_FAR;

    State cur;
    while (!q.empty()) {
	    cur = q.top();
	    if (!loc_known[cur.loc_v] && cur.dist < VERY_FAR) {
		    loc_u = cur.loc_v;
		    shortest_dist = cur.dist;
		    break;
	    } else {
		    q.pop();
	    }
    }
//     fprintf(stderr, "%d\n", loc_u);
    return loc_u;
}

// int Find_min_dist(long long loc_dist[], int loc_known[], int loc_n) {
//     long loc_u = -1, loc_v;
//     long long shortest_dist = VERY_FAR;

//     for (loc_v = 0; loc_v < loc_n; loc_v++) {
//         if (!loc_known[loc_v]) {
//             if (loc_dist[loc_v] < shortest_dist) {
//                 shortest_dist = loc_dist[loc_v];
//                 loc_u = loc_v;
//             }
//         }
//     }
//     return loc_u;
// }

void reduce_gr(Node **nodes, Arc **arcs)
{
	return;
}
