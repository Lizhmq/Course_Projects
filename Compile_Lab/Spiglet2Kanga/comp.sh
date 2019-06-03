#!/bin/bash

tmp1=tmp1.txt
tmp2=tmp2.txt

flist=(BinaryTree BubbleSort Factorial LinearSearch LinkedList MoreThan4 QuickSort TreeVisitor)

make
./gp.sh

for file in ${flist[@]}
do
    ./run.sh ./src/$file.kg > $tmp1
    ./run.sh ./right/$file.kg > $tmp2
    diff $tmp1 $tmp2
done

rm -rf $tmp1 $tmp2