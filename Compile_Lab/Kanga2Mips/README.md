# Kanga to Mips

## Usage

build all files:

<code>make</code>

generate mips file with kanga file:

`./gen.sh input.kg [output.s]`

run mips file with an intepreter:

`./run.sh program.s`

clear all files

`make clean`

hint:

`./run.sh` and `./gen.sh` may need some privilege, try to `chmod` it

if `./run.sh` failed, you may need to install **spim**

## Test

build mips file with kanga test cases:

`./gp.sh`

compare the output of generated mips programs with output of right mips programs:

`./comp.sh`

succeed if no output.