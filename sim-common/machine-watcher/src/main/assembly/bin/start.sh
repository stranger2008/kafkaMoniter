#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf

localIp=`/sbin/ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"`
cpuNums=`curl -m 1 --retry-delay 2 --retry 3 "http://cap.jd.com/flavor/findCpuByIP.do?ip=$localIp"`
gcThreadStr=""
if [ -z "$cpuNums" ] && [ -a "bin/cpuNums" ]; then
	cpuNums=`cat bin/cpuNums`
fi
if [ -n "$cpuNums" ] && [[ "$cpuNums" == +([0-9]) ]] && [ $cpuNums -gt 0 ]; then
	gcThreadStr=" -XX:ParallelGCThreads=$cpuNums -XX:ConcGCThreads="$(($cpuNums/2))
	echo $cpuNums > bin/cpuNums
fi

ERVER_NAME=`sed '/application.name/!d;s/.*=//' conf/conf.properties | tr -d '\r'`
SERVER_PORT=`sed '/server.port/!d;s/.*=//' conf/conf.properties | tr -d '\r'`
LOGS_FILE=`sed '/log4j.file/!d;s/.*=//' conf/conf.properties | tr -d '\r'`S
STD_FILE=`sed '/stdout.name/!d;s/.*=//' conf/conf.properties | tr -d '\r'`
if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ -n "$SERVER_PORT" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
        exit 1
    fi
fi

LOGS_DIR=""
STD_DIR="/export/Logs/machine-watcher"
if [ -n "$STD_FILE" ]; then
    LOGS_DIR=$STD_FILE
else
    LOGS_DIR=$DEPLOY_DIR/logs
fi
if [ ! -d $STD_DIR ]; then
    mkdir -p $STD_DIR
fi
STDOUT_FILE=${STD_DIR}/stdout.log

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi
JAVA_MEM_OPTS=""
BITS=`file $JAVA_HOME/bin/java | grep 64-bit`
if [ -n "$BITS" ]; then
    let memTotal=`cat /proc/meminfo |grep MemTotal|awk '{printf "%d", $2/1024 }'`
    if [ $memTotal -gt 2500 ];then
        JAVA_MEM_OPTS=" -server -Xmx4096m -Xms4096m -Xmn512m -XX:PermSize=256m -Xss2048k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70$gcThreadStr"
    else 
        JAVA_MEM_OPTS=" -server -Xmx1024m -Xms1024m -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70$gcThreadStr"
    fi
else
    JAVA_MEM_OPTS=" -server -Xms1024m -Xmx1024m -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC$gcThreadStr"
fi

echo -e "Starting the $SERVER_NAME ...\c"
nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -classpath $CONF_DIR:$LIB_JARS com.jd.si.jupiter.tools.machinewatcher.Application> $STDOUT_FILE 2>&1 &

COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1 
        COUNT=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
    if [ $COUNT -gt 0 ]; then
        break
    fi
done
echo "OK!"
PIDS=`ps  --no-heading -C java -f --width 1000 | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: ${STDOUT_FILE}"
