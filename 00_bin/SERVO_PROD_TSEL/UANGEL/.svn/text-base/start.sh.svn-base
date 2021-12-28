#!/bin/bash
cd /data03/Cynapse/UANGEL/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

INPROGRESS=inprogressUAN01.txt
CONFIG_FILE=../01_config/CynapseUAN01.cfg
LOG_FILE=../05_log/CynapseUan01.log
BACKUP_DIR=../08_backup/CSCUAN01
LIST_SUBFOLDER="BJB PLB"

if [ -f $INPROGRESS ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi

echo "1" >$INPROGRESS
java -jar Cynapse.jar $CONFIG_FILE


#backup
for SB in $LIST_SUBFOLDER
do
 BK_FILE=${DATE_PARAM:0:8}"_"$SB"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
 LS_FILE=${DATE_PARAM:0:8}"_"$SB"_"`date +%s | sha256sum | base64 | head -c 32`.lst
 ls $BACKUP_DIR/$SB/*.*| awk -F '/' '{print $NF}' > $BACKUP_DIR/$LS_FILE
 tar czf $BACKUP_DIR/$BK_FILE -C  $BACKUP_DIR/$SB/ . --remove-files 
done

./loadUAN01.sh
#maintain csv
/data03/ftpuser1/SERVO_CSV_DUMP/UANGEL/maintain01.sh
rm -f $INPROGRESS
