#!/bin/bash
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

cd /data01/Cynapse/SIEMENSPM2G/00_bin

./getDataPM2GRSIE04.sh $DATE_PARAM
./runPM2GRSIE04.sh  $DATE_PARAM
