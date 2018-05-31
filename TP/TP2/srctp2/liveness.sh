#!/bin/sh

java -cp minijava.jar:. rtl.Liveness short < $1
