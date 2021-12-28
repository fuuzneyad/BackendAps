#!/bin/bash
#skrip ini berfungsi menemukan pattern file dan isi yang ada di dalamnya..
#penggunaaan ./findFilePattern.sh [pattetnFile] [patternIsi1] [patternIsi2]
#contoh menemukan file yang mengandung namafile *2013012407*.xml dan didalamnya mengandung "WCEL-101" dan "Cell_Throughput_WBTS"
#maka run: ./findFilePattern.sh *2013012407*.xml WCEL-101 Cell_Throughput_WBTS

###############################################################################
SOURCE_DIR=../02_raw/3GRNOK01/
DEST_DIR=1


###############################################################################
ARGS=1
if [ $# -ne "$ARGS" ];
then
        echo ""
        echo "   penggunaaan ./findFilePattern.sh [pattetnFile] [patternIsi1] [patternIsi2]"
        echo "   exp: mencari file yang mengandung *2013012407*.xml dan didlmnya mengandung \"WCEL-101\" dan \"Cell_Throughput_WBTS\""
        echo "   run: ./findFilePattern.sh *2013012407*.xml WCEL-101 Cell_Throughput_WBTS"
        echo ""
	exit 0
else
   CONT_PTTRN1=$1
   CONT_PTTRN2=$2
   CONT_PTTRN3=$3

   
unalias cp   
   for f in $SOURCE_DIR/*.xml
   do 
	HEAD=`head -n20 $f`
	if [ -n `echo $HEAD | grep "$CONT_PTTRN1" ` ]; then
	   echo "[not match] $f"
       else
 	   cp -f  $f $DEST_DIR
	   echo "[found] $f"
       fi
   done

fi
