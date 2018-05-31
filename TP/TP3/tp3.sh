#!/bin/sh

javac -cp minijava.jar:. tp3/*.java
java -cp minijava.jar:. tp3.Test < $1 


