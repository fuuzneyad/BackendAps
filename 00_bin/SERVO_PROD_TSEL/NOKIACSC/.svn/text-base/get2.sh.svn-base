#!/bin/bash
HOST=10.2.124.129
USERNAME=servopms
PASSWORD=Tsel2013
REMOTE_DIR=/d/repnb/etload/export
LOCAL_DIR=../02_raw/CSCNOK02

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d%H00'`
else
        DATE_PARAM=$1
fi

FILEPATTERN=etlexpmx_*$DATE_PARAM*.xml

ftp -n $HOST <<END_SCRIPT
        quote USER $USERNAME
        quote PASS $PASSWORD
        lcd $LOCAL_DIR
        prompt
        binary
        cd $REMOTE_DIR
        mget $FILEPATTERN
        quit
END_SCRIPT
