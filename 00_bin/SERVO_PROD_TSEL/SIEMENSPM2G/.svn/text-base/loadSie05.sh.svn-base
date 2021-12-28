#!/bin/bash
cd /data01/Cynapse/SIEMENSPM2G/00_bin

MYSQL_HOST=172.24.1.53
MYSQL_PORT=3308
MYSQL_USERNAME=root
MYSQL_PASSWORD=servo2013
MYSQL_SCHEMA=SERVO_2GRSIE05

LOA_FOLDER=../04_loader/2GRSIE05
SCHEMA=SERVO_2GRSIE05
LIST_FILE=lstSERVO_2GRSIE05.txt
LOG_FOLDER=../05_log/LOAD_SERVO_2GRSIE05.log


#########################################################################

if [ -f $LIST_FILE ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi

#Loa2Csv
./loa2Csv05.sh

ls $LOA_FOLDER/*.loa*  > $LIST_FILE
sleep 20

#Check and run R1 Maintain Partition at 00 o'clock
hour=`mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT -e "select hour(now()) as waktu" | grep -v waktu`
if [ "$hour" == "0" ]; then
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call SET_PARTITION_R1();"
fi

#Truncate Temp if any
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call TRUNCATE_TEMP();"

#Loading loa file
echo start at `date` > $LOG_FOLDER
for F in `cat $LIST_FILE`
do
  TABLE=`basename $F | cut -d- -f1`

  echo `date` loading $TABLE of file $F >> $LOG_FOLDER
  if [ ${TABLE: -5} == "_TEMP" ]; then
    TABLE_ASL=`echo $TABLE | sed 's/_TEMP//g'`
    mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "set sql_log_bin=0; LOAD DATA LOCAL INFILE '$F' INTO TABLE $TABLE_ASL fields terminated by ',' enclosed by '\"' lines terminated by '\n';"
  fi
  mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "set sql_log_bin=0; LOAD DATA LOCAL INFILE '$F' INTO TABLE $TABLE fields terminated by ',' enclosed by '\"' lines terminated by '\n';"

  RET=$?
  if [ "$RET" != "0" ]; then
   echo `date` "error loading $F" >>$LOG_FOLDER
  else
   rm -f $F
  fi
done
echo end at `date` >> $LOG_FOLDER

#Run Precalculation Procedure
  mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call PRECALCULATION1();"

#Check and run Other Maintain Partition at 00 o'clock
if [ "$hour" == "0" ]; then
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call SET_PARTITION();"
fi

rm -f $LIST_FILE
