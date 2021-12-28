#!/bin/bash
cd /data01/Cynapse/ERICSSONPSC/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-7 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi
./getDataPSCSASNERI02.sh $DATE_PARAM
./runPSCSASNERI02.sh $DATE_PARAM
