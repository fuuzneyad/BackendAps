cd /data01/Cynapse/ERICSSONPM3G/00_bin 
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-6 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getData3GRERI02.sh $DATE_PARAM
./run3GRERI02.sh $DATE_PARAM
