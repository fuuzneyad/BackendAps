#!/bin/bash
cd /data01/Cynapse/ERICSSONPSC/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi
./getDataPSCGGSNERIPGW01.sh $DATE_PARAM
./runPSCGGSNERIPGW01.sh $DATE_PARAM
