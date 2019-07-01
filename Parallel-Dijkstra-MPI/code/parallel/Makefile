# CC = g++
CC = mpic++
CCFLAGS = -g -ansi -Wall -O6 -DNDEBUG -I../../lib
#CCFLAGS = -ansi -Wall -O6 -g -I../../lib
#CCFLAGS = -ansi -Wall -O6 -g -DALLSTATS
LDFLAGS = 
LOADLIBES = -lm        # the name used by the automatic linker rule
MLBFLAGS = -DMLB

SRCS = mpi_solver.cpp parser_gr.cc timer.cc
HDRS = values.h nodearc.h
CODES = mpi.out mpiC.out

all: $(CODES)

mpi.out: $(SRCS) $(HDRS) parser_ss.cc
	$(CC) $(CCFLAGS) -o mpi.out $(SRCS) parser_ss.cc $(LOADLIBES)

mpiC.out: $(SRCS) $(HDRS) parser_ss.cc
	$(CC) $(CCFLAGS) -DCHECKSUM -o mpiC.out $(SRCS) parser_ss.cc $(LOADLIBES)

clean:
	rm -f *~ mpi.out mpiC.out
