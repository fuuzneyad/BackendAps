#!/bin/bash
cd /data03/Cynapse/ERICSSONCLI/00_bin

CONFIG_FILE=../01_config/CynapseCLIERISGSN.cfg
LOADFILE=loadCLISGSN.sh
DATE_PARAM=`date '+%Y%m%d'`
RAW_DIR1=../02_raw/SGSN01/
BACKUP_DIR1=../08_backup/SGSN01
INPROGRESS_FILE=inProgressSGSN.txt

if [ -f $INPROGRESS_FILE ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi
echo $DATE_PARAM >$INPROGRESS_FILE

#backup
BK_FILE=${DATE_PARAM:0:8}"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
tar czf $BACKUP_DIR1/$BK_FILE -C  $RAW_DIR1 .


#run parser
java -jar Cynapse.jar $CONFIG_FILE > /dev/null

#./$LOADFILE

rm -f $INPROGRESS_FILE
