#!/bin/bash
cd /data03/Cynapse/NOKIACLI/00_bin

CONFIG_FILE=../01_config/CynapseCLINOKCSC.cfg
LOADFILE=loadCLIMSC.sh
DATE_PARAM=`date '+%Y%m%d'`
RAW_DIR1=../02_raw/CSCNOK01
RAW_DIR2=../02_raw/CSCNOK02
BACKUP_DIR1=../08_backup/CSCNOK01
BACKUP_DIR2=../08_backup/CSCNOK02
INPROGRESS_FILE=inProgressCSC.txt
SKIPPFILE=skippedParserCSC.txt

if [ -f $INPROGRESS_FILE ];
then
   echo "Another Process Detected. Aborting current job..."
   echo $DATE_PARAM >>$SKIPPFILE
   exit 0
fi
echo $DATE_PARAM >$INPROGRESS_FILE

#backup
BK_FILE=${DATE_PARAM:0:8}"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
tar czf $BACKUP_DIR1/$BK_FILE -C  $RAW_DIR1 .
tar czf $BACKUP_DIR2/$BK_FILE -C  $RAW_DIR2 .

#run parser
nohup /usr/bin/java -jar Cynapse.jar $CONFIG_FILE > /dev/null

./$LOADFILE

rm -f $INPROGRESS_FILE
