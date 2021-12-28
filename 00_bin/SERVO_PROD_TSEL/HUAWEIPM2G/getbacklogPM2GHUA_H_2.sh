#!/bin/bash
cd /data02/Cynapse/HUAWEIPM2G/00_bin
LOG1=../05_log/backlog/backlog1_2.txt
LOG2=../05_log/backlog/backlog2_2.txt
LOG3=../05_log/backlog/backlog3_2.txt
LOG4=../05_log/backlog/backlog4_2.txt
LOG5=../05_log/backlog/backlog5_2.txt
LOG6=../05_log/backlog/backlog6_2.txt
tanggal=`date -d '2 day ago' '+%Y%m%d'`

java -jar GetFileHuawei.jar ../01_config/CynapseFTP2GRHUA01.cfg $tanggal > $LOG1
java -jar GetFileHuawei.jar ../01_config/CynapseFTP2GRHUA01_1.cfg $tanggal > $LOG1
java -jar GetFileHuawei.jar ../01_config/CynapseFTP2GRHUA02.cfg $tanggal > $LOG2
java -jar GetFileHuawei.jar ../01_config/CynapseFTP2GRHUA03.cfg $tanggal > $LOG3
java -jar GetFileHuawei.jar ../01_config/CynapseFTP2GRHUA04.cfg $tanggal > $LOG4
java -jar GetFileHuawei.jar ../01_config/CynapseFTP2GRHUA05.cfg $tanggal > $LOG5
java -jar GetFileHuawei.jar ../01_config/CynapseFTP2GRHUA06.cfg $tanggal > $LOG6




