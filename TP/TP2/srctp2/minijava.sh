#!/bin/sh

java -cp minijava.jar:. mj.interpreter.MiniJavaInterpreter < $1
