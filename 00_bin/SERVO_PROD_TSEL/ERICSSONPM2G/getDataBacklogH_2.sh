#!/bin/bash
cd /data01/Cynapse/ERICSSONPM2G/00_bin/
LOG_FILE1=../05_log/yesterday2_eri1.txt
LOG_FILE2=../05_log/yesterday2_eri2.txt
LOG_FILE3=../05_log/yesterday2_eri3.txt
LOG_FILE4=../05_log/yesterday2_eri4.txt
tanggal=`date -d '2 day ago' +'C%Y%m%d'`

/usr/bin/java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTP2GRERI01.cfg $tanggal > $LOG_FILE1
/usr/bin/java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTP2GRERI02.cfg $tanggal > $LOG_FILE2
/usr/bin/java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTP2GRERI03.cfg $tanggal > $LOG_FILE3
/usr/bin/java -jar -Xms512m  ServoGetFiles.jar ../01_config/CynapseFTP2GRERI04.cfg $tanggal > $LOG_FILE4

