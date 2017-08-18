#!/bin/sh

cd /home/cross
DATESTR=`date +%Y%m%d`
rm -f cross.log.$DATESTR
mv cross.log cross.log.$DATESTR


LIB_PATH=./lib/log4j-1.2.17.jar:./lib/mina-core-2.0.7.jar:./lib/slf4j-api-1.7.5.jar
LIB_PATH=$LIB_PATH:./lib/slf4j-log4j12-1.7.5.jar

$JAVA_HOME/bin/java -classpath "./lib/cross.jar:$CLASSPATH:.:$LIB_PATH" Accept843 $@>> cross.log 2>&1 &

echo $! > cross.pid
tail -f cross.log

