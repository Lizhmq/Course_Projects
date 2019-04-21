#!/bin/bash

if [ $# -eq 0 ]; then
    echo
    echo "Run piglet file via a piglet interpreter based on Java"
    echo 
    echo "usage:"
    echo "      ./run.sh [piglet file]"
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

java -jar ./pgi.jar < $1
