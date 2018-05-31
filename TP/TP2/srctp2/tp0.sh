#!/bin/sh

javac -cp minijava.jar:. tp0/*.java
java -cp minijava.jar:. tp0.Test < $1
