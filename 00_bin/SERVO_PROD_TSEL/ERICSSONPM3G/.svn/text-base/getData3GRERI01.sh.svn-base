#!/bin/bash
cd /data01/Cynapse/ERICSSONPM3G/00_bin

CONFIG_FILE=../01_config/CynapseFTP3GRERI01.cfg
LOG_FILE=../05_log/getData3GRERI01.txt

#rm -rf $TEMP_DIR
#mkdir $TEMP_DIR

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+A%Y%m%d.%H'`
else
        DATE_PARAM=A$1
fi

java -jar -Xms512m   ServoGetFiles.jar $CONFIG_FILE $DATE_PARAM > $LOG_FILE


