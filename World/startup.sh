

cd /home/allWorld
DATESTR=`date +%Y%m%d`
rm -f allWorld.log.$DATESTR
mv allWorld.log allWorld.log.$DATESTR


LIB_PATH=./lib/antlr-2.7.6.jar:./lib/asm-3.1.jar:./lib/commons-httpclient-3.1.jar
LIB_PATH=$LIB_PATH:./lib/c3p0-0.9.1.jar:./lib/servlet-api-2.5-20081211.jar
LIB_PATH=$LIB_PATH:./lib/cglib-2.2.jar:./lib/commons-beanutils-core-1.8.0.jar:./lib/commons-codec-1.3.jar:./lib/commons-collections-3.2.jar:./lib/commons-configuration-1.6.jar
LIB_PATH=$LIB_PATH:./lib/commons-digester-1.8.jar:./lib/commons-lang-2.4.jar:./lib/commons-logging-1.1.1.jar:./lib/commons-pool-1.5.6.jar
LIB_PATH=$LIB_PATH:./lib/dom4j-1.6.1.jar:./lib/hibernate3.jar:./lib/ejb3-persistence.jar:./lib/ezmorph-1.0.3.jar:./lib/json-lib-2.4-jdk15.jar
LIB_PATH=$LIB_PATH:./lib/jta.jar:./lib/log4j-1.2.15.jar:./lib/mina-core-2.0.7.jar:./lib/mysql-connector-java-5.0.8-bin.jar
LIB_PATH=$LIB_PATH:./lib/slf4j-api-1.6.1.jar:./lib/slf4j-log4j12-1.6.1.jar:./lib/trove-3.0.0a5.jar:./lib/log4j-1.2.15.jar
LIB_PATH=$LIB_PATH:./lib/commons-primitives-1.0.jar:./lib/ehcache-core-2.4.2.jar:./lib/snsclient.jar
LIB_PATH=$LIB_PATH:./lib/serverengine.jar:./lib/javassist.jar:./lib/java_memcached-release_2.6.0.jar

$JAVA_HOME/bin/java -classpath "./lib/allWorld.jar:$CLASSPATH:.:$LIB_PATH" cyou.akworld.World $@>> allWorld.log 2>&1 &

echo $! > allWorld.pid
tail -f allWorld.log

