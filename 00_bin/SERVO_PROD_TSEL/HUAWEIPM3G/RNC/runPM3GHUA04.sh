#!/bin/bash
cd /data02/Cynapse/HUAWEIPM3G/RNC/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-6 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

################## Must Be Filled!!   ####################################
#type file=>GZ|XML
BACKUP_DIR=../08_backup/3GRHUA04
RAW_DIR=../02_raw/3GRHUA04
CYNAPSE_CONFIG=../01_config/Cynapse3GRHUA04.cfg
LOG_FILE=../05_log/
INPROGRESSFILE=inProgressiRun3GRHUA04.txt
SKIPPEDFILE=skipped3GRHUA04.txt
LOADFILE=loadHua04.sh
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
        gunzip -c $FILE > $RAW_DIR/$EXTRACT_FN
        #echo "XXX $EXTRACT_FN"
	mv -f $FILE $BACKUP_DIR/
  else
	COMPRESSED_FILE=`basename $FILE`".gz"
	gzip -c $FILE > $BACKUP_DIR/$COMPRESSED_FILE
  fi
done

#run parser
java -jar Cynapse.jar $CYNAPSE_CONFIG > /dev/null

#load Data
./$LOADFILE 


#delete INPROGRESSFILE
rm -f $INPROGRESSFILE
