-- Common Schemas..
/*Schema for Vertex*/
CREATE TABLE IF NOT EXISTS VERTEX (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(50) DEFAULT NULL,
	`NE_NAME` varchar(50) DEFAULT NULL,
	`VENDOR` varchar(50) DEFAULT NULL,
	`OWN_SP_DEC` varchar(100) DEFAULT NULL,
	`OWN_GT` text DEFAULT NULL,
	`OWN_MSRN` text DEFAULT NULL,
	`IP` varchar(400) DEFAULT NULL

)Engine=InnoDB;

-- Raw Schemas..
/*Schema for LST_VRFBRDIPBIND*/
CREATE TABLE  IF NOT EXISTS LST_VRFBRDIPBIND (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`SUBRACK` VARCHAR(101),
	`SLOT` VARCHAR(101),
	`BOARD_IP_ADDR` VARCHAR(101),
	`VRF_NAME` VARCHAR(101),
	`Description` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_NSE*/
CREATE TABLE  IF NOT EXISTS LST_NSE (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`NSE_DIRECTION` VARCHAR(101),
	`NSE_ID` VARCHAR(104),
	`BVCI` VARCHAR(101),
	`SUBRACK_NO` VARCHAR(101),
	`SLOT_NO` VARCHAR(101),
	`PROCESS_NO` VARCHAR(101),
	`BSS_NO` VARCHAR(104),
	`FLUSH_TIMER` VARCHAR(102),
	`SUPPORT_PFC` VARCHAR(101),
	`SUPPORT_CBL` VARCHAR(101),
	`SUPPORT_INR` VARCHAR(101),
	`SUPPORT_LCS` VARCHAR(101),
	`SUPPORT_RIM` VARCHAR(101),
	`SUPPORT_PFCFC` VARCHAR(101),
	`INCLUDE_ARP_IE` VARCHAR(101),
	`INCLUDE_RA_CAP_IE` VARCHAR(101),
	`BEARER_TYPE` VARCHAR(101),
	`NSE_CONFIG_TYPE` VARCHAR(101),
	`SUPPORT_GB_FLEX` VARCHAR(101),
	`SUPPORT_SPEC_SC` VARCHAR(101),
	`BSS_SUPPORT_QOS_VER` VARCHAR(101),
	`SUPPORT_MOCN` VARCHAR(101),
	`SUPPORT_SPID` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_GBPAGING*/
CREATE TABLE  IF NOT EXISTS LST_GBPAGING (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`LAI_OF_PAGING` VARCHAR(109),
	`RAC_OF_PAGING` VARCHAR(101),
	`NSE_D` VARCHAR(104),
	`SUBRACK_NO` VARCHAR(101),
	`SLOT_NO` VARCHAR(101),
	`PROCESS_NO` VARCHAR(101)
)Engine=InnoDB;
/*Schema for TST_DNS*/
CREATE TABLE  IF NOT EXISTS TST_DNS (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`IP_ADDR` VARCHAR(101),
	`FQDN` VARCHAR(101),
	`MNC` VARCHAR(103),
	`MCC` VARCHAR(103),
	`RAC` VARCHAR(104),
	`LAC` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_RNC*/
CREATE TABLE  IF NOT EXISTS LST_RNC (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`RNC_INDEX` VARCHAR(101),
	`RNC_NAME` VARCHAR(101),
	`MCC` VARCHAR(103),
	`MNC` VARCHAR(102),
	`RNC_IDENTIFIER` VARCHAR(101),
	`NETWORK_INDICATOR` VARCHAR(101),
	`SPC` VARCHAR(101),
	`RELOC_ALOC_TIMER` VARCHAR(105),
	`RELOC_COMPLETE_TIMER` VARCHAR(105),
	`RAB_ASSIG_TIMER` VARCHAR(105),
	`IGNORED_OVERLOAD_TIMER` VARCHAR(102),
	`INCREASE_TRAFFIC_TIMER` VARCHAR(102),
	`RESET_TIMER` VARCHAR(102),
	`RESET_ACK_TIMER` VARCHAR(102),
	`CORE_MCC` VARCHAR(101),
	`CORE_MNC` VARCHAR(101),
	`CORE_NET_IDENTFR` VARCHAR(105),
	`RNC_SUPPORT_IMS` VARCHAR(101),
	`RNC_SUPPORT_IUFLEX` VARCHAR(101),
	`RNC_SUPPORT_RAN_SHARE` VARCHAR(101),
	`RESERVED_PARAM` VARCHAR(101),
	`RNC_PROTOCOL_VER` VARCHAR(101),
	`RNC_SUPPORT_SPID` VARCHAR(101),
	`RNC_SUPPORT_OUT_OF_RAN` VARCHAR(101),
	`MOST_TIME_MSG_SEND_RESET` VARCHAR(101),
	`RNC_SUPPOR_IPV6` VARCHAR(101),
	`SEND_OVERLOAD_MSG` VARCHAR(101),
	`RNC_SUPPORT_ONE_TUNNEL` VARCHAR(101),
	`RNC_SUPPORT_R7_QOS` VARCHAR(101),
	`NEGOTIABLE_RAB_QOS` VARCHAR(101),
	`ALERNATIVE_BITRATE_TYPE` VARCHAR(101),
	`CHG_SYM_ASYM` VARCHAR(101),
	`SGSN_BUFFER_3G_DOWNLINK` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_IUPAGING*/
CREATE TABLE  IF NOT EXISTS LST_IUPAGING (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`LAI` VARCHAR(109),
	`ROUTING_AREA_CODE` VARCHAR(101),
	`RNC_INDEX` VARCHAR(101)
)Engine=InnoDB;
/*Schema for DSP_S1APLNK*/
CREATE TABLE  IF NOT EXISTS DSP_S1APLNK (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`SUBRACK_NO` VARCHAR(101),
	`SLOT_NO` VARCHAR(101),
	`PROCESS_NO` VARCHAR(101),
	`MCC` VARCHAR(103),
	`MNC` VARCHAR(102),
	`IP_ADDR_TYPE1` VARCHAR(101),
	`ENODEB_IP_ADDR1` VARCHAR(101),
	`IP_ADDR_TYPE2` VARCHAR(101),
	`ENODEB_IP_ADDR2` VARCHAR(101),
	`ENODEB_PORT` VARCHAR(105),
	`ENODEB_ID_TYPE` VARCHAR(101),
	`ENODEB_ID` VARCHAR(106),
	`ENODEB_NAME` VARCHAR(101),
	`S1APLE_INDEX` VARCHAR(101),
	`LINK_STATUS` VARCHAR(101),
	`SUBRACK1_S1AP` VARCHAR(101),
	`SLOT1_S1AP` VARCHAR(101),
	`SUBRACK2_S1AP` VARCHAR(101),
	`SLOT2_S1AP` VARCHAR(101),
	`SUBRACK3_S1AP` VARCHAR(101),
	`SLOT3_S1AP` VARCHAR(101),
	`SUBRACK4_S1AP` VARCHAR(101),
	`SLOT4_S1AP` VARCHAR(101),
	`NUM_OF_INTMITTEN_DISCONECT` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_S1PAGING*/
CREATE TABLE  IF NOT EXISTS LST_S1PAGING (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`SUBRACK_NO` VARCHAR(101),
	`SLOT_NO` VARCHAR(101),
	`PROCESS_NO` VARCHAR(101),
	`MCC_MNC` VARCHAR(105),
	`ENODEB_ID_TYPE` VARCHAR(101),
	`ENODEB_ID` VARCHAR(106),
	`TRACKING_AREA_ID` VARCHAR(109)
)Engine=InnoDB;
/*Schema for TST_DNS_NAPTR*/
CREATE TABLE  IF NOT EXISTS TST_DNS_NAPTR (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`FQDN` VARCHAR(101),
	`RESOLVE_TYPE` VARCHAR(101),
	`HOSTNAME_NUM` VARCHAR(101),
	`HOSTNAME` VARCHAR(101),
	`ENTITY` VARCHAR(101),
	`INTERFACE` VARCHAR(101),
	`S5_PROTOCOL` VARCHAR(101),
	`S8_PROTOCOL` VARCHAR(101),
	`TTL` VARCHAR(105),
	`IP_NUM` VARCHAR(101),
	`IP_ADDR1` VARCHAR(101),
	`OTHER_IP_ADDR` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_TALST*/
CREATE TABLE  IF NOT EXISTS LST_TALST (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`TRACKING_AREA_LIST_ID` VARCHAR(104),
	`TAI` VARCHAR(109),
	`Description` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_TAILAI*/
CREATE TABLE  IF NOT EXISTS LST_TAILAI (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`BEGIN_TAI` VARCHAR(109),
	`END_TAI` VARCHAR(109),
	`SUBSCRIBER_RANGE` VARCHAR(101),
	`IMSI_PREFIX` VARCHAR(101),
	`LAI` VARCHAR(109)
)Engine=InnoDB;
/*Schema for LST_MMEID*/
CREATE TABLE  IF NOT EXISTS LST_MMEID (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`MOBILE_COUNTRY_CODE` VARCHAR(103),
	`MOBILE_NETWORK_CODE` VARCHAR(102),
	`MME_GROUP_IDENTITY` VARCHAR(104),
	`MME_CODE_BEGIN` VARCHAR(102),
	`NUMBER_OF_MME_CODES` VARCHAR(101)
)Engine=InnoDB;
/*Schema for LST_LOCALNRI*/
CREATE TABLE  IF NOT EXISTS LST_LOCALNRI (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`POOL_ID` VARCHAR(104),
	`NRI_VALUE_BEGIN` VARCHAR(102),
	`NRI_NUMBER` VARCHAR(101),
	`NRI_STATE` VARCHAR(109)
)Engine=InnoDB;
/*Schema for DSP_USRPDPNUM*/
CREATE TABLE  IF NOT EXISTS DSP_USRPDPNUM (
	`ENTRY_DATE` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	`SOURCE_ID` varchar(100) DEFAULT '',
	`DATETIME_ID` datetime NULL DEFAULT '0000-00-00 00:00:00',
	`NE_ID` varchar(200) DEFAULT NULL,
	`LINE` Integer(20) DEFAULT 0,
	`COMMAND_PARAM` varchar(100) DEFAULT NULL,
	`SUBRACK_NO` VARCHAR(101),
	`SLOT_NO` VARCHAR(101),
	`PROCESS_NO` VARCHAR(101),
	`STATIC_USER_NUMBER` VARCHAR(104),
	`APN_CONF_NUMBER` VARCHAR(105),
	`NUMBER_OF_DYNAMIC_2G_MMS` VARCHAR(104),
	`NUMBER_OF_DYNAMIC_3G_MMS` VARCHAR(104),
	`NUMBER_OF_DYNAMIC_4G_MMS` VARCHAR(104),
	`NUMBER_OF_DYNAMIC_2G_PDP` VARCHAR(103),
	`NUMBER_OF_DYNAMIC_3G_PDP` VARCHAR(104),
	`NUMBER_OF_DYNAMIC_4G_BEARER` VARCHAR(104)
)Engine=InnoDB;