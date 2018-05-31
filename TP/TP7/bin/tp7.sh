#!/bin/sh

javac -cp minijava.jar:. tp7/*.java
java -cp minijava.jar:. tp7.Test < $1 


