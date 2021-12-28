#!/bin/bash
cd /data03/Cynapse/NOKIAPM3G/00_bin

INPROGRESS=inProgressGet3GRNOK01.txt
CONFIG_FILES="../01_config/CynapseFTP3GRNOK01.cfg"
LOGFILE=../05_log/getData3GRNOK01.log

#################################################################################################################

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-6 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi


if [ -f $INPROGRESS ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi
echo "$DATE_PARAM" >$INPROGRESS

rm -f $LOGFILE
for CONF in $CONFIG_FILES
do
 java -jar ServoGetFiles.jar $CONF $DATE_PARAM > $LOGFILE
done

rm -f $INPROGRESS
