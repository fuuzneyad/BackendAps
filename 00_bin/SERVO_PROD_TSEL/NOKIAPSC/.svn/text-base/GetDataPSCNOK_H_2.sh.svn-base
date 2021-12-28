#!/bin/bash
cd /data03/Cynapse/NOKIAPSC/00_bin
LOG_FILE=../05_log/yesterday2.txt
tanggal=`date -d '2 day ago' '+%Y%m%d'`


java -jar ServoGetFiles.jar ../01_config/CynapseFTPPSCSGSNNOK01.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTPPSCSGSNNOK02.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTPPSCGGSNNOK01.cfg $tanggal > $LOG_FILE
