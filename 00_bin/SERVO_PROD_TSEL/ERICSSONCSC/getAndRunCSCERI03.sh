#!/bin/bash
cd /data01/Cynapse/ERICSSONCSC/MSS/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getDataCSCERI03.sh "$DATE_PARAM"
./runCSCERI03.sh  "$DATE_PARAM"
