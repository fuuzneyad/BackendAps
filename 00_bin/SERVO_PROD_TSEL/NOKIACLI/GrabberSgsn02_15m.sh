#!/bin/bash

cd /data03/Cynapse/NOKIACLI/00_bin

CONF=../01_config/GrabberSGSN02_15m.cfg
LOGFILE=../05_log/GrabberSGSN02_15m.log
INPROGRESS=inprogressGrabberSGSN02_15m.txt

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