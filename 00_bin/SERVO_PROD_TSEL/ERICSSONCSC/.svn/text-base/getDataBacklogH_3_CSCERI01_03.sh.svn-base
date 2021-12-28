#!/bin/bash
cd /data01/Cynapse/ERICSSONCSC/MSS/00_bin 
LOG_FILE=../05_log/yesterday3_CSCERI01_03.txt
tanggal=`date -d '3 day ago' '+%Y%m%d'`

java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTPCSCERI01.cfg $tanggal > $LOG_FILE
java -jar -Xms512m  ServoGetFilesSQS.jar ../01_config/CynapseFTPCSCSqsERI01.cfg $tanggal > $LOG_FILE
java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTPCSCERI03.cfg $tanggal > $LOG_FILE
java -jar -Xms512m  ServoGetFilesSQS.jar ../01_config/CynapseFTPCSCSqsERI03.cfg $tanggal > $LOG_FILE
