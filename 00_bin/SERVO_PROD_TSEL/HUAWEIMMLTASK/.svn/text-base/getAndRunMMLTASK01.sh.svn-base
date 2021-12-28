#!/bin/bash
cd /data03/Cynapse/HUAWEIMMLTASK/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-4 date '+%Y-%m-%d`
else
        DATE_PARAM=$1
fi

./getDataMMLTASK01.sh $DATE_PARAM
./runMMLTASK01.sh $DATE_PARAM
