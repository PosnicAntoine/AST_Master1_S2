#!/bin/sh

java -cp minijava.jar:. mj.interpreter.Compiler < $1
