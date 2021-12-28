#!/bin/bash
cd /data01/Cynapse/SIEMENSPM2G/00_bin
LOG_FILE=../05_log/yesterday1.txt
tanggal=`date -d '1 day ago' +'%Y%m%d'`

java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRSIE01.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRSIE02.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRSIE03.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRSIE04.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRSIE05.cfg $tanggal > $LOG_FILE
java -jar ServoGetFiles.jar ../01_config/CynapseFTP2GRSIE06.cfg $tanggal > $LOG_FILE
