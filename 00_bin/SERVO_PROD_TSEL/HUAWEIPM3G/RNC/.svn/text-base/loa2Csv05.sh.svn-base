#!/bin/bash

LOA_FILE=../04_loader/3GRHUA05
DEST_DIR=/data02/ftpuser1/SERVO_CSV_DUMP/HUAWEIPM3G/3GRHUA05
HASH=`date +%s | sha256sum | base64 | head -c 32`
CURRENT=`pwd`

for FL in $LOA_FILE/*loa*
do
  TABLE=`echo $(basename $FL) |cut -d_ -f2 | cut -d- -f1`
  TANGGAL=`echo $(basename $FL) | cut -d- -f2 | cut -d. -f1`
  TANGGAL=`echo ${TANGGAL:0:10}`
  cat $FL >> $DEST_DIR/$TABLE-$TANGGAL.csv
  echo $FL $TABLE $TANGGAL
done

mv $LOA_FILE/*.hdr $DEST_DIR

echo "compressing..."
TGLS=`ls $DEST_DIR/*.csv | cut -d- -f2 | cut -d. -f1 | sort | uniq | egrep -o '[[:digit:]]{8}' | sort | uniq`
cd $DEST_DIR

for TGL in $TGLS
do
 BK_FILE=$TGL"_"$HASH.tar.gz
 LST_FILE=$TGL"_"$HASH.lst
 if [ ! -d "$DEST_DIR/$TGL" ]; then
  echo "directory $DEST_DIR/$TGL not exits, creating it"
  mkdir "$DEST_DIR/$TGL"
 fi
  echo "compressing $TGL"
  ls *$TGL*.csv >$DEST_DIR/$TGL/$LST_FILE
  tar czf $DEST_DIR/$TGL/$BK_FILE *$TGL*.csv *.hdr ##--remove-files
  rm -f *$TGL*.csv
done

rm -f $DEST_DIR/*.hdr
cd $CURRENT
