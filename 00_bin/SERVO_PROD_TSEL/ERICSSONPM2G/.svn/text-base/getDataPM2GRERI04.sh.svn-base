#!/bin/bash
cd /data01/Cynapse/ERICSSONPM2G/00_bin
LOG_FILE=../05_log/GetFiles04.log

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+C%Y%m%d'`
else
        DATE_PARAM=$1
fi

/usr/bin/java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTP2GRERI04.cfg  $DATE_PARAM > $LOG_FILE
