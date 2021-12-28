#!/bin/bash
cd /data03/Cynapse/HUAWEIMMLTASK/00_bin
LOG_FILE=../05_log/GetFiles01.log

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y-%m-%d'`
else
        DATE_PARAM=$1
fi

java -jar -Xms512m ServoGetFiles.jar ../01_config/CynapseFTP01.cfg  $DATE_PARAM > $LOG_FILE
