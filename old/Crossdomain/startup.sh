#!/bin/bash

cd /home/cross
DATESTR=`date +%Y%m%d`
rm -f stdout.log.$DATESTR
mv stdout.log stdout.log.$DATESTR

#tail -f stdout.log

LIB_PATH=./lib/log4j-1.2.17.jar:./lib/mina-core-2.0.7.jar:./lib/slf4j-api-1.7.5.jar
LIB_PATH=$LIB_PATH:./lib/slf4j-log4j12-1.7.5.jar

$JAVA_HOME/bin/java -classpath "./lib/cross.jar:$CLASSPATH:.:$LIB_PATH" Accept843 $@>> stdout.log 2>&1 &

echo $! > cross.pid
tail -f stdout.log

