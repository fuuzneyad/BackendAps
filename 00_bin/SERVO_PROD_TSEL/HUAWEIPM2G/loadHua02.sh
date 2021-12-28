#!/bin/bash
cd /data02/Cynapse/HUAWEIPM2G/00_bin

MYSQL_HOST=172.24.1.52
MYSQL_PORT=3308
MYSQL_USERNAME=root
MYSQL_PASSWORD=servo2013
MYSQL_SCHEMA=SERVO_2GRHUA02

LOA_FOLDER=../04_loader/2GRHUA02
LIST_FILE=lstSERVO_2GRHUA02.txt
LOG_FOLDER=../05_log/LOAD_SERVO_2GRHUA02.log


#########################################################################

if [ -f $LIST_FILE ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi

#loa2Csv
./loa2Csv02.sh

ls $LOA_FOLDER/*.loa*  > $LIST_FILE
rm -f $LOA_FOLDER/*1275071620*
sleep 20

#Check and run R1 Maintain Partition at 00 o'clock
hour=`mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT -A -e "select hour(now()) as waktu" | grep -v waktu`
if [ "$hour" == "0" ]; then
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT -A $MYSQL_SCHEMA -e "call SET_PARTITION_R1();"
fi

#Loading loa file
echo start at `date` > $LOG_FOLDER
for F in `cat $LIST_FILE`
do 
  TABLE=`basename $F | cut -d- -f1`

  echo `date` loading $TABLE of file $F >> $LOG_FOLDER
  mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT -A $MYSQL_SCHEMA -e "set sql_log_bin=0; LOAD DATA LOCAL INFILE '$F' INTO TABLE $TABLE fields terminated by ',' enclosed by '\"' lines terminated by '\n';"

  rm -f $F
done
echo end at `date` >> $LOG_FOLDER

#Check and run Other Maintain Partition at 00 o'clock
if [ "$hour" == "0" ]; then
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT -A $MYSQL_SCHEMA -e "call SET_PARTITION();"
fi

rm -f $LIST_FILE
