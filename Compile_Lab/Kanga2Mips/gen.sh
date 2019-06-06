#!/bin/bash

prog=K2M
if [ $# -eq 0 ]; then
    echo
    echo "Generate mips file via a kanga compiler based on Java"
    echo 
    echo "usage:"
    echo "      ./gen.sh {kanga file} [mips file]"
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

if [ $# -eq 1 ]; then

    java K2M $1
    exit
fi

if [ $# -eq 2 ]; then
    java K2M $1 $2
    exit
fi