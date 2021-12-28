#!/bin/bash
cd /data03/Cynapse/NOKIAPM2G/00_bin
LOG_FILE=../05_log/yesterday2.txt
tanggal=`date -d '2 day ago' '+%Y%m%d'`

/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK01.cfg $tanggal > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK02.cfg $tanggal > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK03.cfg $tanggal > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK04.cfg $tanggal > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK05.cfg $tanggal > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK06.cfg $tanggal > $LOG_FILE
