#!/bin/sh

java -cp minijava.jar:. rtl.interpreter.Eval < $1
