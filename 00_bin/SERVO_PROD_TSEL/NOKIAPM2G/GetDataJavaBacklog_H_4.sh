#!/bin/bash
cd /data03/Cynapse/NOKIAPM2G/00_bin
LOG_FILE=../05_log/yesterday3.txt
tanggal=`date -d '5 day ago' '+%Y%m%d'`
tanggal1=`date -d '4 day ago' '+%Y%m%d'`
tanggal2=`date -d '3 day ago' '+%Y%m%d'`
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK02.cfg $tanggal > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK02.cfg $tanggal1 > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK02.cfg $tanggal2 > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK06.cfg $tanggal > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK06.cfg $tanggal1 > $LOG_FILE
/usr/bin/java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRNOK06.cfg $tanggal2 > $LOG_FILE
