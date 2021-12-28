#!/bin/bash
cd /data01/Cynapse/ERICSSONPM3G/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-6 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

################## Must Be Filled!!   ####################################
#type file=>GZ|XML
BACKUP_DIR=../08_backup/3GRERI01
RAW_DIR=../02_raw/3GRERI01
CYNAPSE_CONFIG=../01_config/Cynapse3GRERI01.cfg
LOG_FILE=../05_log/
INPROGRESSFILE=inProgressiRun3GRERI01.txt
SKIPPEDFILE=skipped3GRERI01.txt
LOADFILE=loadEri01.sh
##########################################################################

if [ -f $INPROGRESSFILE ];
then
   echo "Another Process Detected. Aborting current job..."
   echo $DATE_PARAM >> $SKIPPEDFILE
   exit 0
fi
echo $DATE_PARAM >  $INPROGRESSFILE

#Backup
BK_FILE=${DATE_PARAM:0:8}"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
LS_FILE=${DATE_PARAM:0:8}"_"`date +%s | sha256sum | base64 | head -c 32`.lst
ls $RAW_DIR/*.*| awk -F '/' '{print $NF}' > $BACKUP_DIR/$LS_FILE
tar czf $BACKUP_DIR/$BK_FILE -C  $RAW_DIR .

#run parser
java -jar Cynapse.jar $CYNAPSE_CONFIG > /dev/null

#loa2csv
./loa2Csv01.sh

#load Data
./$LOADFILE
 
#delete INPROGRESSFILE
rm -f $INPROGRESSFILE
