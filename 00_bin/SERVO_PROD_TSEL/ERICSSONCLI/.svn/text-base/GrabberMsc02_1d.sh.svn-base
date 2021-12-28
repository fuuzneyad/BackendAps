#!/bin/bash

cd /data03/Cynapse/ERICSSONCLI/00_bin

CONF=../01_config/GrabberMss02_1d.cfg
LOGFILE=../05_log/GrabberMss02_1d.log
INPROGRESS=inprogressGrabberMss02_15m.txt

STRING=`ps ux | grep -v grep | grep $CONF | awk '{print $2}'`
if [ -n "$STRING" ]; then
   echo "Grabber $CONF Still running.. exiting"
   echo `date `"Grabber $CONF Still running.. exiting" > $LOGFILE
   kill $STRING
fi

echo `date `> $INPROGRESS
java -jar TelnetGrabberMSS.jar $CONF >>$LOGFILE
rm -f $INPROGRESS
