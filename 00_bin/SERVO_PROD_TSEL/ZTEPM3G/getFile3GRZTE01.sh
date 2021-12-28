cd /data02/Cynapse/ZTEPM3G/00_bin
LOG_FILE=../05_log/GetData3GRZTE01.log
CONFIG_FILE=../01_config/CynapseFTP3GRZTE01.cfg
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%d%b%Y'`
else
        DATE_PARAM=$1
fi

java -jar ServoGetFiles.jar $CONFIG_FILE $DATE_PARAM > $LOG_FILE
