#!/bin/sh

fullfilename=$1
dirname=$(dirname "$fullfilename")
filename=$(basename "$fullfilename")
fname="${filename%.*}"
ext="${filename##*.}"

#echo "Input File: $fullfilename"
#echo "Dirname: $dirname"
#echo "Filename without Path: $filename"
#echo "Filename without Extension: $fname"
#echo "File Extension without Name: $ext"

./regalloc.sh $fullfilename > $fname.alloc.rtl

./rtl.sh $fullfilename > $fname.rtl.log

./rtl.sh $fname.alloc.rtl > $fname.alloc.rtl.log

#rm -f $fname.rtl

if diff $fname.rtl.log $fname.alloc.rtl.log >/dev/null ; then
  echo $fname: ok
else
  echo $fname: fail
fi
