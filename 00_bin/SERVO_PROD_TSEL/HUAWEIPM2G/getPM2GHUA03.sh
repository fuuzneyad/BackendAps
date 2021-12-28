#!/bin/bash
cd /data02/Cynapse/HUAWEIPM2G/00_bin

INPROGRESS=inProgressGetHua03.txt
CONFIG_FILES="../01_config/CynapseFTP2GRHUA03.cfg"
LOGFILE=../05_log/getData03.log

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
 java -jar GetFileHuawei.jar $CONF $DATE_PARAM >> $LOGFILE
done
rm -f $INPROGRESS
