#!/bin/bash
cd /data01/Cynapse/ERICSSONPSC/00_bin
LOG_FILE=../05_log/yesterday2.txt
tanggal=`date -d '2 day ago' '+%Y%m%d'`

java -jar ServoGetFilesGgsn.jar ../01_config/CynapseFTPPSCGGSNERI01.cfg $tanggal > $LOG_FILE
java -jar ServoGetFilesGgsn.jar ../01_config/CynapseFTPPSCGGSNERIPGW01.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTPPSCSGSNERI01.cfg $tanggal > $LOG_FILE
