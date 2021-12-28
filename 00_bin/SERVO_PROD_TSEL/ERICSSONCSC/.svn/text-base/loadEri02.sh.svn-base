#!/bin/bash
cd /data01/Cynapse/ERICSSONCSC/MSS/00_bin

MYSQL_HOST=172.24.1.51
MYSQL_PORT=3308
MYSQL_USERNAME=root
MYSQL_PASSWORD=servo2013
MYSQL_SCHEMA=SERVO_CSCERI02

LOA_FOLDER=../04_loader/CSCERI02
SCHEMA=SERVO_CSCERI02
LIST_FILE=lstSERVO_CSCERI02.txt
LOG_FOLDER=../05_log/LOAD_SERVO_CSCERI02.log


#########################################################################

if [ -f $LIST_FILE ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi

#loa2csv
./loa2Csv02.sh

ls $LOA_FOLDER/*.loa*  > $LIST_FILE
sleep 20

#Check and run R1 Maintain Partition at 00 o'clock
hour=`mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT -e "select hour(now()) as waktu" | grep -v waktu`
if [ "$hour" == "0" ]; then
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call SET_PARTITION_R1();"
fi

#Loading loa file
echo start at `date` > $LOG_FOLDER
for F in `cat $LIST_FILE`
do 
  TABLE=`basename $F | cut -d- -f1`

  echo `date` loading $TABLE of file $F >> $LOG_FOLDER
  mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "set sql_log_bin=0; LOAD DATA LOCAL INFILE '$F' INTO TABLE $TABLE fields terminated by ',' enclosed by '\"' lines terminated by '\n';"

  rm -f $F
done
echo end at `date` >> $LOG_FOLDER

#Check and run Other Maintain Partition at 00 o'clock
if [ "$hour" == "0" ]; then
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call SET_PARTITION();"
fi
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call SET_DELTA_R1();"
rm -f $LIST_FILE
