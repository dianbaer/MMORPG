#!/bin/bash

cd /home/mrd/animalkingdom/world
cp ./lib/* ./lib1
DATESTR=`date +%Y%m%d`
rm -f stdout.log.$DATESTR
mv stdout.log stdout.log.$DATESTR

#tail -f stdout.log

LIB_PATH=./lib1/antlr-2.7.6.jar:./lib1/asm-3.1.jar:./lib1/commons-httpclient-3.1.jar
LIB_PATH=$LIB_PATH:./lib1/c3p0-0.9.1.jar
LIB_PATH=$LIB_PATH:./lib1/cglib-2.2.jar:./lib1/:./lib1/commons-beanutils-core-1.8.0.jar:./lib1/commons-codec-1.3.jar:./lib1/commons-collections-3.2.jar:./lib1/commons-configuration-1.6.jar
LIB_PATH=$LIB_PATH:./lib1/commons-digester-1.8.jar:./lib1/commons-lang-2.4.jar:./lib1/commons-logging-1.1.1.jar:./lib1/commons-pool-1.5.6.jar
LIB_PATH=$LIB_PATH:./lib1/dom4j-1.6.1.jar:./lib1/hibernate3.jar:./lib1/ejb3-persistence.jar:./lib1/ezmorph-1.0.3.jar:./lib1/json-lib-2.4-jdk15.jar
LIB_PATH=$LIB_PATH:./lib1/jta.jar:./lib1/log4j-1.2.15.jar:./lib1/mina-core-1.1.7.jar:./lib1/mysql-connector-java-5.0.8-bin.jar
LIB_PATH=$LIB_PATH:./lib1/slf4j-api-1.6.0.jar:./lib1/slf4j-log4j12-1.6.1.jar:./lib1/trove-3.0.0a5.jar:./lib1/log4j-1.2.15.jar
LIB_PATH=$LIB_PATH:./lib1/commons-primitives-1.0.jar:./lib1/ehcache-core-2.4.2.jar:./lib1/java_memcached-release_2.5.1.jar
LIB_PATH=$LIB_PATH:./lib1/serverengine.jar:./lib1/animalkingdomserver.jar:./lib1/javassist.jar

$JAVA_HOME/bin/java -server -Xms500m -Xmx500m -XX:ThreadPriorityPolicy=42 -verbose:gc -XX:PermSize=128m -XX:MaxPermSize=128m -XX:+PrintGCDetails  -XX:+PrintGCTimeStamps  -XX:+UseFastAccessorMethods -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:MaxTenuringThreshold=31 -XX:+DisableExplicitGC -Djava.awt.headless=true -classpath "./lib1/world.jar:$CLASSPATH:.:$LIB_PATH" cyou.akworld.AKWorld $@>> stdout.log 2>&1 &

echo $! > animalworld.pid
tail -f stdout.log

