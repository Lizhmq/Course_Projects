#!/bin/bash

if [ $# -eq 0 ]; then
    echo
    echo "Generate kanga file via a spiglet compiler based on Java"
    echo 
    echo "usage:"
    echo "      ./gen.sh {spiglet file} [outputfile]"
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

    java S2K $1
    exit
fi

if [ $# -eq 2 ]; then
    java S2K $1 $2
    exit
fi