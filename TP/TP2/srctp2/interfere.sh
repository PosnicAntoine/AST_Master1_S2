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

java -cp minijava.jar:. rtl.graph.InterferenceGraph < $1 


