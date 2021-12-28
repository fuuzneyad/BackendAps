#!/bin/bash

if [ -z $1 ]; then
        echo "Usage with Param Y/N"
        echo "example ./setRPC.sh Y"
	echo ""
	exit 0
else
        PARAM=$1        
	if [ $PARAM=="Y" ]; then
	  NEGPAR=N
	else
	  NEGPAR=Y
	fi
fi

sed -i "s/RPC_IS_ACTIVE=$NEGPAR/RPC_IS_ACTIVE=$PARAM/g" *.cfg
