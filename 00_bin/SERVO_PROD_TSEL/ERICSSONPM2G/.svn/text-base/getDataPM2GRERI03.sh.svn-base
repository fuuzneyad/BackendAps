#!/bin/bash
cd /data01/Cynapse/ERICSSONPM2G/00_bin
LOG_FILE=../05_log/GetFiles03.log

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+C%Y%m%d'`
else
        DATE_PARAM=$1
fi

java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTP2GRERI03.cfg  $DATE_PARAM > $LOG_FILE
java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTP2GRERI03_BTBT2.cfg $DATE_PARAM >> $LOG_FILE
