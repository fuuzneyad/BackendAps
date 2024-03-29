#!/bin/bash

cd /data03/Cynapse/NOKIACLI/00_bin

CONF=../01_config/GrabberMGW02.cfg
LOGFILE=../05_log/GrabberMGW02.log
INPROGRESS=inprogressGrabberMgw02_15m.txt

#STRING=`ps ux | awk '/$CONF/ && !/awk/ {print $2}'`
STRING=`ps ux | grep -v grep | grep $CONF | awk '{print $2}'`
if [ -n "$STRING" ]; then
   echo "Grabber $CONF Still running.. exiting"
   echo `date `"Grabber $CONF Still running.. exiting" > $LOGFILE
   kill $STRING
fi

echo `date `> $INPROGRESS
java -jar CynapseGrabber.jar $CONF >>$LOGFILE
rm -f $INPROGRESS
