#!/bin/bash

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+C%Y%m%d.%H0'`
else
        DATE_PARAM=$1
fi

################## Must Be Filled!!   ####################################
H_DIR=/data01/Cynapse/ERICSSONCSC/MSS/00_bin
BACKUP_DIR=../08_backup/CSCERI01
BACKUPSQS_DIR=../08_backup/CSCSQSERI01
RAW_DIR=../02_raw/CSCERI01
RAWSQS_DIR=../02_raw/CSCSQSERI01
CONFIG_FILE=../01_config/CynapseCSCERI01.cfg
SQS_CONFIG_FILE=../01_config/CynapseCSCSQSERI01.cfg
INPROGRESSFILE=inProgressCSCERI01.txt
SKIPPEDFILE=skippedCSCERI01.txt
LOADFILE=loadEri01.sh

##########################################################################
cd $H_DIR

#Check if annother Process Detected
if [ -f $INPROGRESSFILE ]; 
then
    echo "Another Process Detected. Aborting current job..."
   echo $DATE_PARAM >> $SKIPPEDFILE
   exit 0
fi
echo $DATE_PARAM >  $INPROGRESSFILE

#TMP backup
if [ ! -d "$BACKUP_DIR/TMP" ]; then
  mkdir $BACKUP_DIR/TMP
fi

if [ ! -d "$BACKUPSQS_DIR/TMP" ]; then
  mkdir $BACKUPSQS_DIR/TMP
fi

#/usr/local/bin/unber-ing files
for f in `ls $RAW_DIR/* | grep -v .unb`
do
    /usr/local/bin/unber $f > $RAW_DIR/$(basename $f).unb
    mv -f $f $BACKUP_DIR/TMP
done

#Compressing backup
BK_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.tar.gz
LS_FILE=$DATE_PARAM"_"`date +%s | sha256sum | base64 | head -c 32`.lst

ls $BACKUP_DIR/TMP/*.*| awk -F '/' '{print $NF}' > $BACKUP_DIR/$LS_FILE
tar czf $BACKUP_DIR/$BK_FILE -C  $BACKUP_DIR/TMP . --remove-files
ls $RAWSQS_DIR/*.*| awk -F '/' '{print $NF}' > $BACKUPSQS_DIR/$LS_FILE
tar czf $BACKUPSQS_DIR/$BK_FILE -C  $RAWSQS_DIR .

java -jar Cynapse.jar $CONFIG_FILE 
java -jar Cynapse.jar $SQS_CONFIG_FILE

#maintain partition
#hour=`mysql -uroot -psuren13 -h$HOST -e "select hour(now()) as waktu" | grep -v waktu`
#if [ "$hour" == "0" ]; then
#  mysql -uroot -psuren13 -h$HOST $SCHEMA -e "call maintainPartition()" 
#fi
#mysql -uroot -psuren13 -h$HOST $SCHEMA -e "call replicateTempToRaw()"

./$LOADFILE

#delete INPROGRESSFILE
rm -f $INPROGRESSFILE
