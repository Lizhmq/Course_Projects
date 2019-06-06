#!/bin/bash

if [ $# -eq 0 ]; then
    echo
    echo "Run mips file via a mips interpreter"
    echo 
    echo "usage:"
    echo "      ./run.sh [mips file]"
    echo 
    exit
fi

if [ ! -f $1 ]; then
    echo
    echo "File $1 doesn't exist"
    echo "Please enter a valid input file"
    echo
    exit
fi

spim -file $1
