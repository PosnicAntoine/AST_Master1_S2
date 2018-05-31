#!/bin/sh

javac -cp minijava.jar:. tp6/*.java
java -cp minijava.jar:. tp6.Test < $1 


