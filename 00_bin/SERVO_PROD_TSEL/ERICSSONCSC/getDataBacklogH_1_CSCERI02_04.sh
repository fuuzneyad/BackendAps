#!/bin/bash
cd /data01/Cynapse/ERICSSONCSC/MSS/00_bin 
LOG_FILE=../05_log/yesterdayCSCERI02_04.txt
tanggal=`date -d '1 day ago' '+%Y%m%d'`

java -jar -Xms512m  ServoGetFilesSQS.jar ../01_config/CynapseFTPCSCMGWERI02.cfg $tanggal > $LOG_FILE
java -jar -Xms512m  ServoGetFilesSQS.jar ../01_config/CynapseFTPCSCMGWERI04.cfg $tanggal > $LOG_FILE
