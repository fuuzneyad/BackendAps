cd /data03/Cynapse/NOKIAPSC/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`date  '+%Y%m%d'`
else
        DATE_PARAM=$1
fi
./GetDataPSCSGSN02.sh $DATE_PARAM
./runPSCSGSNNOK02.sh $DATE_PARAM
