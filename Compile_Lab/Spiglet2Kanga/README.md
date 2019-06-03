# Spiglet to Kanga

## Usage

build all files:

<code>make</code>

generate kanga file with spiglet file:

`./gen.sh input.spg [output.kg]`

run kanga file with an intepreter:

`./run.sh program.kg`

clear all files

`make clean`

hint:

`./run.sh` and `./gen.sh` may need some privilege, try to `chmod` it

## Test

build kanga file with spiglet test cases:

`./gp.sh`

compare the output of generated kanga programs with output of right kanga programs

`./comp.sh`