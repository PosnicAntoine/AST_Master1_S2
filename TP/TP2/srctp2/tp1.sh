#!/bin/sh

javac -cp minijava.jar:. tp1/*.java
java -cp minijava.jar:. tp1.Test < $1
