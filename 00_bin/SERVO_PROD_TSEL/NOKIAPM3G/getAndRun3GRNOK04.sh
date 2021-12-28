#!/bin/bash
cd /data03/Cynapse/NOKIAPM3G/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-6 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getData3GRNOK04.sh $DATE_PARAM
./runPM3GRNOK04.sh $DATE_PARAM
