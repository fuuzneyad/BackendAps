#!/bin/bash
cd /data01/Cynapse/SIEMENSPM2G/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

################## Must Be Filled!!   ####################################
BACKUP_DIR=../08_backup/2GRSIE06
RAW_DIR=../02_raw/2GRSIE06
CYNAPSE_CONFIG=../01_config/Cynapse2GRSIE06.cfg
LOG_FILE=../05_log/
INPROGRESSFILE=inProgressiRun2GRSIE06.txt
SKIPPEDFILE=skipped2GRSIE06.txt
LOADFILE=loadSie06.sh
##########################################################################

if [ -f $INPROGRESSFILE ];
then
   echo "Another Process Detected. Aborting current job..."
   echo $DATE_PARAM >> $SKIPPEDFILE
   exit 0
fi
echo $DATE_PARAM >  $INPROGRESSFILE

#Extracting if gz..
#for FILE in $RAW_DIR/*.*
#do
#  if  [ ${FILE: -3} == ".gz" ]; then
#        EXTRACT_FN=`basename $FILE .gz`
#        gunzip $FILE
#  fi
#done

#run parser
java -jar Cynapse.jar $CYNAPSE_CONFIG > /dev/null

#BACKUP
BK_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
LS_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.lst
ls $BACKUP_DIR/TMP/*.*| awk -F '/' '{print $NF}' > $BACKUP_DIR/$LS_FILE
tar czf $BACKUP_DIR/$BK_FILE -C  $BACKUP_DIR/TMP . --remove-files

#load Data
./$LOADFILE 


#delete INPROGRESSFILE
rm -f $INPROGRESSFILE
