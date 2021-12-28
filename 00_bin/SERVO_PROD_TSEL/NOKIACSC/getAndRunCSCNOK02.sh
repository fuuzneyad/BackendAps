#!/bin/bash
cd /data01/Cynapse/NOKIACSC/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d%H'`
else
        DATE_PARAM=$1
fi

./get2.sh $DATE_PARAM
./runPMCSCNOK02.sh $DATE_PARAM
