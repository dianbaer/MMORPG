#!/bin/bash

cd /home/mrd/animalkingdom/cross
cp ./lib/* ./lib1
DATESTR=`date +%Y%m%d`
rm -f stdout.log.$DATESTR
mv stdout.log stdout.log.$DATESTR

#tail -f stdout.log

LIB_PATH=./lib1/log4j-1.2.17.jar:./lib1/mina-core-2.0.7.jar:./lib1/slf4j-api-1.7.5.jar
LIB_PATH=$LIB_PATH:./lib1/slf4j-log4j12-1.7.5.jar

$JAVA_HOME/bin/java -classpath "./lib1/cross.jar:$CLASSPATH:.:$LIB_PATH" Accept843 $@>> stdout.log 2>&1 &

echo $! > animalworld.pid
tail -f stdout.log

