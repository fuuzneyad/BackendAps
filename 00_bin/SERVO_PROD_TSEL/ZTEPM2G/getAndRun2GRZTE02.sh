#!/bin/bash
cd /data02/Cynapse/ZTEPM2G/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%d%b%Y'`
else
        DATE_PARAM=$1
fi


./getFile2GRZTE02.sh $DATE_PARAM
./run2GRZTE02.sh $DATE_PARAM
