#!/bin/bash
cd /data01/Cynapse/ERICSSONPM2G/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getDataPM2GRERI04.sh "$DATE_PARAM"
./runPM2GERI04.sh  "$DATE_PARAM"
