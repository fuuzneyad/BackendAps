#!/bin/bash
cd /data/Cynapse/ERICSSONCLI/00_bin

MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USERNAME=root
MYSQL_PASSWORD=servo2013
MYSQL_SCHEMA=DASHBOARD_CLI

LOA_FOLDER=../04_loader/DASHBOARD_CLI
SCHEMA=DASHBOARD_CLI
LIST_FILE=lstSERVO_ERISGSNCLI01.txt
LOG_FOLDER=../05_log/LOAD_SERVO_ERISGSNCLI01.log


#########################################################################

if [ -f $LIST_FILE ];
then
   echo "Another Process Detected. Aborting current job..."
   exit 0
fi

ls $LOA_FOLDER/*.loa*  > $LIST_FILE

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

#Maintain data
 mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call DeleteOlder();"
#Run Precalculation Procedure
mysql -u$MYSQL_USERNAME -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT $MYSQL_SCHEMA -e "call ERICSSON_MSC_CLI();"


rm -f $LIST_FILE
