#!/bin/bash
cd /data01/Cynapse/ERICSSONPSC/00_bin 
LOG_FILE=../05_log/GetFilesGgsn01.log

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

java -jar ServoGetFilesGgsn.jar ../01_config/CynapseFTPPSCGGSNERI01.cfg  $DATE_PARAM > $LOG_FILE
