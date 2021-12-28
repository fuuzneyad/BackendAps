#!/bin/bash
cd /data01/Cynapse/ERICSSONPM3G/00_bin
LOG_FILE=../05_log/yesterday1.txt
tanggal=`date -d '1 day ago' '+%Y%m%d'`

java -jar -Xms512m ServoGetFiles.jar ../01_config/CynapseFTP3GRERI01.cfg $tanggal > $LOG_FILE
java -jar -Xms512m ServoGetFiles.jar ../01_config/CynapseFTP3GRERI02.cfg $tanggal > $LOG_FILE
java -jar -Xms512m ServoGetFiles.jar ../01_config/CynapseFTP3GRERI03.cfg $tanggal > $LOG_FILE 
