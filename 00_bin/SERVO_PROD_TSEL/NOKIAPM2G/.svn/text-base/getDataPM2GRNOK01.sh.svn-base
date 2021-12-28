#!/bin/bash
HOST=10.2.113.150
USERNAME=vasepher
PASSWORD=Toyota2013
REMOTE_DIR=/d/corenb/bsspmm/client
LOCAL_DIR=../02_raw/2GRNOK01

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-3 date '+%Y%m%d%H00'`
else
        DATE_PARAM=$1
fi

FILEPATTERN=BSC*.*$DATE_PARAM*

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
#sleep 20
