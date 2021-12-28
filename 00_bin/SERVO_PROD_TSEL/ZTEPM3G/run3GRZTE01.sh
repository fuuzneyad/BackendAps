#!/bin/bash
cd /data02/Cynapse/ZTEPM3G/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%d%b%Y'`
else
        DATE_PARAM=$1
fi

################## Must Be Filled!!   ####################################
BACKUP_DIR=../08_backup/3GRZTE01
RAW_DIR=../02_raw/3GRZTE01
CYNAPSE_CONFIG=../01_config/Cynapse3GRZTE01.cfg
LOG_FILE=../05_log/
INPROGRESSFILE=inProgressiRun3GRZTE01.txt
SKIPPEDFILE=skipped3GRZTE01.txt
LOADFILE=loadZte01.sh
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



#BACKUP
BK_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
LS_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.lst
ls $RAW_DIR/*.*| awk -F '/' '{print $NF}' > $BACKUP_DIR/$LS_FILE
tar czf $BACKUP_DIR/$BK_FILE -C  $RAW_DIR .

java -jar Cynapse.jar $CYNAPSE_CONFIG > /dev/null
#load Data
./$LOADFILE 


#delete INPROGRESSFILE
rm -f $INPROGRESSFILE
