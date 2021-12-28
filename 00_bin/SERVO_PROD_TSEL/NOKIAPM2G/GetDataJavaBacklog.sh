#!/bin/bash
cd /data03/Cynapse/NOKIAPM2G/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`date -d '1 day ago' '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK01.cfg $DATE_PARAM > ../05_log/Backlog2GRNOK01.log
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK02.cfg $DATE_PARAM > ../05_log/Backlog2GRNOK02.log
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK03.cfg $DATE_PARAM > ../05_log/Backlog2GRNOK03.log
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK04.cfg $DATE_PARAM > ../05_log/Backlog2GRNOK04.log
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK05.cfg $DATE_PARAM > ../05_log/Backlog2GRNOK05.log
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK06.cfg $DATE_PARAM > ../05_log/Backlog2GRNOK06.log
