#!/bin/bash

if [ $# -eq 0 ]; then
    echo
    echo "Generate piglet file via a minijava compiler based on Java"
    echo 
    echo "usage:"
    echo "      ./gen.sh [minijava file]"
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

java Main $1
