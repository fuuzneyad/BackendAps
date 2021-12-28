#!/bin/bash
cd /data02/Cynapse/HUAWEIPMCSC/00_bin 
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-4 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getCSCHUA01.sh $DATE_PARAM
./runCSCHUA01.sh $DATE_PARAM
