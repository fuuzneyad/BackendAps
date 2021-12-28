#!/bin/bash
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-3 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

cd /data01/Cynapse/SIEMENSPM2G/00_bin

LOG=../05_log/GetFIle04.log
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRSIE04.cfg $DATE_PARAM >$LOG
