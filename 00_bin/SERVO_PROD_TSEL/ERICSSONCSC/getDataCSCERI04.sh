#!/bin/bash
cd /data01/Cynapse/ERICSSONCSC/MSS/00_bin
LOG_FILE=../05_log/GetFiles04.log

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

java -jar -Xms512m  ServoGetFilesSQS.jar ../01_config/CynapseFTPCSCMGWERI04.cfg  $DATE_PARAM > $LOG_FILE
