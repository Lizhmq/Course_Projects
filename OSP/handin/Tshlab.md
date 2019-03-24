## Tshlab

### Analysis Report

#### Purpose

To become more familiar with process control and signalling by finishing a tiny linux shell.

Work--complete functions below: 

* eval() : main routine that parses and interprets the cmdline
* sigchld_handler, sigint_handler, sigtstp_handler

Requirements:

* redirection of input & output

* pipes not needed

* built-commands & job control

  * quit
  * jobs
  * bg/fg job

* *reap all of its zombie chidren*

#### File Hierachy

tshlab/src/				Main source codes, some of which will be copied to handout and sent to students

tshlab/writeup/			Writeup of Lab sent to students

tshlab/patches/			Patches added by PKU // to fix bugs ?

tshlab/test/				Test

tshlab/test-autograder/		Test

tshlab/online-solutions/		Online solutions // may be used for cheat checker ?

tshlab/handin/				Directory of handins from students

tshlab/handout/			Directory sent to student

tshlab-rubric.txt			Other code rules

tshlab/Makefiles and other config files for autolab

#### Main Part -- tshlab/src

This directory contains the source for the driver program to test the tiny shell.

* *runtrace.c* is the program used to run and test *tsh*. It forks a child to execve *tsh*, and the parent process uses socket pairs to send signal and data to child process*(tsh)*. The *datafd* socket pair is used to send and receive datas, in other words, the input and output of *tsh*. The *syncfd* socket pair is used to send and receive synchronize signals(i.e. WAIT SIGNAL).

  This synchronization can prevent from race condition. For example, parent process sends "/bin/myspin1 10" to *tsh* through *datafd* and then hope to send a SIGINT to it. But when the SIGINT arrives, *tsh* may not have started *myspin1*, then the *myspin1* never stops and time out. So we send a SIGNAL from *myspin1* and call WAIT in parent process before we send SIGINT.

  <img src="./Pre/pics/t.png" />

* sdriver.c is the main routine that runs the driver. 

  The driver tests the functionality of a shell by calling *runtrace* for multiple times. It uses trace files as input, and compare the output of *tsh* with that from the *tshref*, ignoring blank spaces and PID. We consider the *tsh* right on some trace if and only if the output matches.

  <img src="./Pre/pics/s.png" />

* trace*.c are trace files used to test the tiny shell. The details of them are discussed in writeup.

* my*.c are helper programs used by traces.

* *fork.c is the wrapper for fork that introduces non-determinism in the order that parent and child are executed.*

* elide.pl is used to elide code between "\$begin \<handout>" and "$end \<handout>" to generate tsh-handout.c for students.

* tsh-broken.c is a broken version of tsh which uses 'tcsetpgrp'.

#### Upgrade

**fix bug**

* In function builtin_cmd(), opened file descriptor isn't closed.

  <img src="./Pre/pics/b.png" />

* Gcc error -- sprintf buffer isn't big enough.

  <img src="./Pre/pics/c.png" />

* When job numbers exceed MAXJOBS, segmentation fault happens.

  <img src="./Pre/pics/d.png" />

* make "pkill" only kill its process.

  <img src="./Pre/pics/e.png" />

**pipe**

Make the *tsh* support pipes.

If *tsh* support pipes, then one job may consists of several processes(connected by pipes). So we modify the job_t struct, add PGID, change pid to pids[].

To monitor the real *shell* in linux, we add these requirements:

* Child processes should be forked by *tsh*, not the child of *tsh*.
* Child processes should share the same PGID, which is different of the PGID of *tsh*.
* A job is considered completed only if all of its processes have finished.

*Attention: There will be race condition between setpgid(0, 0) and setpgid(0, pid0); process b and c should call setpgid after process a.*

<img src="./Pre/pics/h.png" />

Additionally, we use SIGUSR1 to synchronize and prevent from race condition.

<img src="./Pre/pics/i.png" />

For simplicity, *tsh* need not to support built-in commands with pipes.

**Job struct & perl**

Modify job struct and related operations(e.g. printf).

<img src="./Pre/pics/l.png" />

Modify sdriver to filter "(pid, pid, ..., pid)".

<img src="./Pre/pics/m.png" />

**trace**

fix trace10:

<img src="./Pre/pics/k.png" />

Add more traces to test for pipes:

* *trace25*	Pipe - use commands with a pipe to xargs
* *trace26*	Pipe - test if children are forked by tsh
* *trace27*	Pipe - background jobs with pipe
* *trace28*	Pipe - use commands with multiple pipes

There are four traces which test for pipes at 4 pts each. Only one trace tests for multiple pipes.

#### Source Code & Git Repositroy & Writeup

See git repository.

<img src="./Pre/pics/a.png" />

#### To-do List

* Builtin-cmd & pipes

  For simplicity, current tsh doesn't support builtin-cmd with pipes.

* Multi-process control

  For foreground job, tsh will wait for all processes to terminate. In other words, a job is considered terminated if all of its processes has terminated.

  But what's the status of a job if some processes in it are stopped while others are running? This is undefined in tsh.

* Background job read/write

  Jobs in tsh is actually back ground job in bash(tsh is the foreground job), so they cann't read/write from/to stdin/stdout. Otherwise SIGTTIN/SIGTTOUT will be raised.

* Evaluation

  We judge the tsh by its output which may be improved by other methods.