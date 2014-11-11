#!/bin/bash

APPLICATION=xfresh-basket-example
LANG=ru_RU.UTF8

exec -a "$APPLICATION" \
    java -cp $( echo lib/*.jar . | sed 's/ /:/g') \
     -Dfile.encoding=UTF8 \
     -Dorg.apache.commons.logging.LogFactory=org.apache.commons.logging.impl.Log4jFactory \
     -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl \
     -showversion -server -Xverify:none \
     -Xmx512m -Xms128m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError \
     net.sf.xfresh.util.Starter beans.xml >> $APPLICATION.log 2>&1 &

echo $! > $APPLICATION.pid

echo $APPLICATION started with PID $!