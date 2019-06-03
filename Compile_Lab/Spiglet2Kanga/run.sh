#!/bin/bash

if [ $# -eq 0 ]; then
    echo
    echo "Run kanga file via a kanga interpreter based on Java"
    echo 
    echo "usage:"
    echo "      ./run.sh [kanga file]"
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

java -jar ./kgi.jar < $1
