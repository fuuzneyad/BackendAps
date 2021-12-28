#!/bin/bash
cd /data03/Cynapse/HUAWEISCP/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

INPROGRESS=inprogressHuaScp01.txt
CONFIG_FILE=../01_config/CynapseHUASCP01.cfg
LOG_FILE=../05_log/CynapseHua01.log
BACKUP_DIR=../08_backup/CSCHUA01

if [ -f $INPROGRESS ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi

echo "1" >$INPROGRESS
java -jar Cynapse.jar $CONFIG_FILE


#backup
 BK_FILE=${DATE_PARAM:0:8}"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
 LS_FILE=${DATE_PARAM:0:8}"_"`date +%s | sha256sum | base64 | head -c 32`.lst
 ls $BACKUP_DIR/TMP/*.*| awk -F '/' '{print $NF}' > $BACKUP_DIR/$LS_FILE
 tar czf $BACKUP_DIR/$BK_FILE -C  $BACKUP_DIR/TMP/*.* . --remove-files 

./loadHuaScp01.sh

#csv maintain
/data03/ftpuser1/SERVO_CSV_DUMP/HUAWEISCP/maintain01.sh

rm -f $INPROGRESS
