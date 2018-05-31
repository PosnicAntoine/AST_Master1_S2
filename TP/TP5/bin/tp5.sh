#!/bin/sh

javac -cp minijava.jar:. tp5/*.java
java -cp minijava.jar:. tp5.Test < $1 


