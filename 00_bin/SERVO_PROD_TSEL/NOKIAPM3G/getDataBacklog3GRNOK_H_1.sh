#!/bin/bash
cd /data03/Cynapse/NOKIAPM3G/00_bin
LOG_FILE=../05_log/yesterday1.txt
tanggal=`date -d '1 day ago' '+%Y%m%d'`

java -jar ServoGetFiles.jar ../01_config/CynapseFTP3GRNOK01.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP3GRNOK02.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP3GRNOK03.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP3GRNOK04.cfg $tanggal > $LOG_FILE
