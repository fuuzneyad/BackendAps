cd /data03/Cynapse/NOKIAPSC/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`date -d '1 day ago' '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

java -jar ServoGetFiles.jar ../01_config/CynapseFTPPSCSGSNNOK01.cfg $DATE_PARAM > ../05_log/PSCSGSNNOK01.log
