###########################################################
#Praxis Cynapse Configuration
#v1.0
#Copyright 2012 @SMLTechnologies
###########################################################
PARSER_CONFIG                   =../01_config/ParserConfig.xml


MODUL_NAME_2                    =3GRHUA02
#---Get Part---
GET_FTP_1                       =Y
FTP_HOST_1                      =10.2.160.124
FTP_PORT_1                      =21
FTP_USERNAME_1                  =ftpuser
FTP_PASSWD_1                    =Changeme_123
FTP_FILEPATTERN_1               =A(.*)($NE).xml.gz
FTP_DATEPATTERN_1               =(.*)
FTP_REMOTE_DIR_1                =/export/home/omc/var/fileint/pmneexport/NodeB
FTP_SUBREMOTE_DIR_1             =
FTP_LOCAL_DIR_1                 =../02_raw/3GRHUA02/
FTP_CHECK_ALREADY_DWL_1         =Y
OUTPUT_CONFIG_1                 =../01_config/OutputMethod2.cfg

