#!/bin/sh

jshell --class-path `cat ./build/tmp/compileJava/source-classes-mapping.txt`:target/classes
