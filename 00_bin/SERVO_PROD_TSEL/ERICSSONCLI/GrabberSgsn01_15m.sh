#!/bin/bash

cd /data03/Cynapse/ERICSSONCLI/00_bin

CONF=../01_config/GrabberSgsn01_15m.cfg
LOGFILE=../05_log/GrabberGsgsn01_15m.log
INPROGRESS=inprogressGrabberSgsn01_15m.txt

STRING=`ps ux | grep -v grep | grep $CONF | awk '{print $2}'`
if [ -n "$STRING" ]; then
   echo "Grabber $CONF Still running.. exiting"
   echo `date `"Grabber $CONF Still running.. exiting" > $LOGFILE
   kill $STRING
fi

echo `date `> $INPROGRESS
java -jar TelnetGrabberSGSN.jar $CONF >>$LOGFILE
rm -f $INPROGRESS
