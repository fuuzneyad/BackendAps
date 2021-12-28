#!/bin/bash
cd /data02/Cynapse/HUAWEIPM3G/RNC/00_bin
tanggal=`date -d '1 day ago' '+%Y%m%d'`

java -jar GetFileHuawei.jar ../01_config/CynapseFTP3GRHUA01.cfg $tanggal
java -jar GetFileHuawei.jar ../01_config/CynapseFTP3GRHUA02.cfg $tanggal
java -jar GetFileHuawei.jar ../01_config/CynapseFTP3GRHUA03.cfg $tanggal
java -jar GetFileHuawei.jar ../01_config/CynapseFTP3GRHUA04.cfg $tanggal
java -jar GetFileHuawei.jar ../01_config/CynapseFTP3GRHUA05.cfg $tanggal


