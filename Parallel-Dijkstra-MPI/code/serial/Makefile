CC = g++

CCFLAGS = -ansi -Wall -O6 -DNDEBUG -I../../lib
#CCFLAGS = -ansi -Wall -O6 -g -I../../lib
#CCFLAGS = -ansi -Wall -O6 -g -DALLSTATS
LDFLAGS = 
LOADLIBES = -lm        # the name used by the automatic linker rule
MLBFLAGS = -DMLB

ASRC = dij_solver.cpp dij.cpp parser_gr.cc timer.cc
AHDR = nodearc.h values.h dij.h nodearc.h dij.h
CODES = dij.out dijC.out

all: $(CODES)

dij.out: $(ASRC) $(AHDR) parser_ss.cc
	$(CC) $(CCFLAGS) -o dij.out $(ASRC) parser_ss.cc $(LOADLIBES)

dijC.out: $(ASRC) $(AHDR) parser_ss.cc
	$(CC) $(CCFLAGS) -DCHECKSUM -o dijC.out $(ASRC) parser_ss.cc $(LOADLIBES)

clean:
	rm -f *~ dij.out dijC.out
