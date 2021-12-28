#!/bin/bash
cd /data01/Cynapse/ERICSSONPSC/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-6 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

################## Must Be Filled!!   ####################################
#type file=>GZ|XML
BACKUP_DIR=../08_backup/PSCGGSNERI01
RAW_DIR=../02_raw/PSCGGSNERI01
CYNAPSE_CONFIG=../01_config/CynapsePSCGGSNERI01.cfg
LOG_FILE=../05_log/
INPROGRESSFILE=inProgressiRunPSCGGSNERI01.txt
SKIPPEDFILE=skippedPSCGGSNERI01.txt
LOADFILE=loadEri01.sh
##########################################################################

if [ -f $INPROGRESSFILE ];
then
   echo "Another Process Detected. Aborting current job..."
   echo $DATE_PARAM >> $SKIPPEDFILE
   exit 0
fi
echo $DATE_PARAM >  $INPROGRESSFILE

#Extracting if gz..
for FILE in $RAW_DIR/*.*
do
  if  [ ${FILE: -3} == ".gz" ]; then
        EXTRACT_FN=`basename $FILE .gz`
        gunzip $FILE
  fi
done

#Backuping..
BK_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
LS_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.lst
ls $RAW_DIR/*.*| awk -F '/' '{print $NF}' > $BACKUP_DIR/$LS_FILE
tar czf $BACKUP_DIR/$BK_FILE -C  $RAW_DIR .

#run parser
java -jar Cynapse.jar $CYNAPSE_CONFIG > /dev/null

#load Data
./$LOADFILE


#delete INPROGRESSFILE
rm -f $INPROGRESSFILE
