package id.co.telkom.parser;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import id.co.telkom.parser.common.propreader.ParserPropReader;

public class ParserManager {
	private static final Logger logger = Logger.getLogger(ParserManager.class);
	private final Map<String, ParserObject> mapParser = new LinkedHashMap<String, ParserObject>();
	
	public ParserManager(){
		//List Both Parser And AbstractInitiator here!!
	try{
		
		//PM
		mapParser.put("HUAPMXML1.0", new ParserObject(//RNC&BSC
				Class.forName("id.co.telkom.parser.entity.pm.huawei.HuaweiPM2G3GParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Huawei BSS 2G XML v.1.0"));
		mapParser.put("HUAPMXML1.1", new ParserObject(//NODEB
				Class.forName("id.co.telkom.parser.entity.pm.huawei.HuaweiPM3GNodeBParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Huawei BSS 3G XML v.1.0"));
		mapParser.put("HUAPSCSGSN1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.huawei.HuaweiPSCSgsnParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Huawei PS Core CSV v.1.0"));
		mapParser.put("HUACSC1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.huawei.HuaweiCSCParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Huawei CS Core CSV v.1.0"));
		mapParser.put("2GRERIASN1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 2G ASN.1 v.1.0"));
		mapParser.put("2GRERIASN1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 2G ASN.1 v.1.1"));
		mapParser.put("2GRERIASN1.2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberParser12"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 2G ASN.1 v.1.2"));
		mapParser.put("2GRERIASN1.3", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberParser13"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 2G ASN.1 v.1.3"));
//		mapParser.put("2GRERIASN1.4.0", new ParserObject(
//				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonAsn1Bouncy11"),
//				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
//				"Ericsson BSS 2G ASN.1 v.2.0 Bouncy"));
		mapParser.put("CSCERIASN1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberCSCParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CS Core ASN.1 v.1.0"));
		mapParser.put("CSCERIASN1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CS Core ASN.1 v.1.1"));
		mapParser.put("CSCERIASN1.3", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberCSCParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CS Core ASN.1 v.1.3"));
		mapParser.put("CSCERIASN1.2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonUnberParser12"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CS Core ASN.1 v.1.2"));
		mapParser.put("CSCERISQS1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonSqsParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CS Core SQS Ascii v.1.0"));
		mapParser.put("3GRERI1.0", new ParserObject(//old
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPM3GEriV10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 3G Xml v.1.0"));
		mapParser.put("3GRERI1.1", new ParserObject(//new w buffering
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPM3GEriV11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 3G Xml v.1.1, buffering"));
		mapParser.put("3GRERI1.2", new ParserObject(//new rlLine w buffering, time diff in hour
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPM3GEriV12"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 3G Xml v.1.2, rln buffering, timediff hour"));
		mapParser.put("3GRERI1.3", new ParserObject(//new rlLine w buffering, time diff in minute
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPM3GEriV13"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson BSS 3G Xml v.1.3, rln buffering, timediff minute"));
		mapParser.put("PSCGGSNERI1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPM3GEriV11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson PS Core GGSN Xml v.1.1"));
		mapParser.put("CSCMGWERI1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPM3GEriV10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CS Core MGW Xml v.1.0"));
		mapParser.put("CSCMGWERI1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPM3GEriV11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CS Core MGW Xml v.1.1"));
		mapParser.put("PSCERISGSN1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPSCSgsnParserV11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson PS Core SGSN Xml v.1.1"));
		mapParser.put("PSCERISGSN1.2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPSCSgsnParserV12"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson PS Core SGSN Xml v.1.2"));
		mapParser.put("PSCGGSNPGW1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPSCGgsnPgw"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson PS Core GGSN PGW Xml v.1.1"));	
		mapParser.put("PSCERISASN1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPSCSasnParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson PS Core SASN Ascii v.1.0"));
		mapParser.put("PSCERISASN1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.ericsson.EricssonPSCSasnParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson PS Core SASN Ascii v.1.1"));
		mapParser.put("2GRNOKXML1.1", new ParserObject(//nokia PM 2G xml
				Class.forName("id.co.telkom.parser.entity.pm.nokia.Nokia2GRXmlParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia BSS 2G Xml v.1.1"));
		mapParser.put("2GRNOKXML1.2", new ParserObject(//nokia PM 2G xml
				Class.forName("id.co.telkom.parser.entity.pm.nokia.Nokia2GRXmlParser12"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia BSS 2G Xml v.1.2"));
		mapParser.put("2GRNOKASC1.0", new ParserObject(//nokia PM 2G ascii
				Class.forName("id.co.telkom.parser.entity.pm.nokia.Nokia2GRAsciiParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia BSS 2G Ascii v.1.0"));
		mapParser.put("PSCNOKSGSN1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.nokia.NokiaPSCNokAsciiParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia PS Core SGSN Ascii v.1.0"));
		mapParser.put("3GRNOKXML1.0", new ParserObject(//read one by one map..
				Class.forName("id.co.telkom.parser.entity.pm.nokia.Nokia3GRXmlParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia BSS 3G Xml v.1.0"));
		mapParser.put("3GRNOKXML1.1", new ParserObject(//read once map at a time..
				Class.forName("id.co.telkom.parser.entity.pm.nokia.Nokia3GRXmlParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia BSS 3G Xml v.1.1"));
		mapParser.put("PSCNOKXML1.2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.nokia.NokiaPSCXmlParser12"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia PS Core Xml v.1.2"));
		mapParser.put("PSCNOKXML1.3", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.nokia.NokiaPSCXmlParser13"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia PS Core Xml v.1.3"));
		mapParser.put("CSCNOKXML1.2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.nokia.NokiaCSCXmlParser12"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia CS Core Xml v.1.2"));
		mapParser.put("CSCNOKXML1.3", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.nokia.NokiaCSCXmlParser13"),
				Class.forName("id.co.telkom.parser.entity.pm.nokia.model.NokiaCSCInitiator"),
				"Nokia PS Core Xml v.1.3"));
		mapParser.put("ZTECSV1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.zte.ZteCsvParser10"),
				Class.forName("id.co.telkom.parser.entity.pm.zte.CynapseZteInitiator"),
				"ZTE BSS 2G/3G CSV v.1.0"));
		mapParser.put("ZTECSV1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.zte.ZteCsvParser11"),
				Class.forName("id.co.telkom.parser.entity.pm.zte.CynapseZteInitiator"),
				"ZTE BSS 2G/3G CSV v.1.2"));
		mapParser.put("2GRSIEASC1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.siemens.SiemensPM2GParser"),
				Class.forName("id.co.telkom.parser.entity.pm.siemens.ParserSiemensInitiator"),
				"Siemens BSS BR8-9 2G Ascii v.1.1"));
		mapParser.put("UANGEL1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.uangel.UangelSCPParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Uangel SCP CSV v.1.0"));
		mapParser.put("HUASCP1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.huawei.HuaweiScpParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Huawei SCP CSV v.1.0"));
		
		//Availibility Servo
		mapParser.put("AVAILSERVO1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.availmover.AvailMover"),
				Class.forName("id.co.telkom.parser.entity.availmover.AvailMoverInitiator"),
				"Servo Availibility 1.0"));
		
		//PRTG
		mapParser.put("PRTG1.0", new ParserObject(//all meas in one file
				Class.forName("id.co.telkom.parser.entity.pm.prtg.PrtgServoV1"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"PRTG XML 1.0"));
		mapParser.put("PRTG1.1", new ParserObject(//core PS
				Class.forName("id.co.telkom.parser.entity.pm.prtg.PrtgServoV2"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"PRTG XML 1.1"));
		mapParser.put("PRTG1.2", new ParserObject(//transport
				Class.forName("id.co.telkom.parser.entity.pm.prtg.PrtgTransportV1"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"PRTG XML 1.2"));
		mapParser.put("PRTG1.3", new ParserObject(//transport
				Class.forName("id.co.telkom.parser.entity.pm.prtg.PrtgTransportV2"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"PRTG XML 1.3"));
		mapParser.put("PRTG1.4", new ParserObject(//transport
				Class.forName("id.co.telkom.parser.entity.pm.prtg.PrtgTransportV3"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"PRTG XML 1.4"));
		
		//CM
		mapParser.put("CMHUACFGMML1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.huawei.HuaweiCMParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Huawei CFGMML v1.0"));
		mapParser.put("CMNOKXML1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.nokia.NokiaCMParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Nokia XML v1.0"));
		mapParser.put("CMNOKXML1.1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.nokia.NokiaCMParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Nokia XML v1.1"));
		mapParser.put("CMSIEASCII1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.siemens.SiemensCMParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) BSS 2G Siemens v1.0"));
		mapParser.put("CMSIESYMBNAME1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.siemens.SiemensCMSymbolicName10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Siemens Symbolic Name v1.0"));
		mapParser.put("CMHUAFEAT1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.huawei.HuaweiLincenceParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Huawei Feature/Licence v1.0"));
		mapParser.put("CMNOKLICFEAT1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.nokia.NokiaCMFeatureLicence"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Nokia CLI Feature/Licence v1.0"));
		mapParser.put("CMNERI3G1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.ericsson.EricssonCM3GParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Ericsson 3G XML v1.0"));
		mapParser.put("CMNERI2G1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.ericsson.EricsoonCM2GParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) Ericsson 2G v1.0"));
		mapParser.put("CMNZTE2G1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.zte.Zte2GCMParserV1"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) ZTE 2G v1.0"));
		mapParser.put("CMNZTE3G1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.zte.Zte2GCMParserV1"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) ZTE 3G v1.0"));
		mapParser.put("CMNHUANODEB1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cm.huawei.HuaweiCMNodebParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Conf Mgmt(CM) ZTE 3G v1.0"));
		
		//dashboard
		mapParser.put("MKIOS1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.mkios.MkiosV1"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"MKIOS performance v.1.0"));
		mapParser.put("USSD1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.ussd.UssdParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"USSD Performance v1.0"));
		mapParser.put("CUSTCOMPLAINT1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.customercomplain.CustomerComplainParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"CWC Customer Complain txt v1.0"));
		mapParser.put("CUSTCOMPLAINT2.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.customercomplain.CwcXmlParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"CWC Customer Complain xml v1.0"));
		mapParser.put("CUSTCOMPLAINT3.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.customercomplain.CwcXmlParser20"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"CWC Customer Complain xml v1.0"));
		mapParser.put("UANGELCDR10", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.uangel.UangelScpCdrParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Uangel CDR v1.0"));
		mapParser.put("RANIXLS10", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.rani.RaniXlsParserV1"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RANI Dashboard Xls/Xlsx physical data"));
		mapParser.put("RANIXLS11", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.rani.RaniXlsParserV2"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RANI Dashboard Xls/Xlsx bad spot"));
		mapParser.put("RAOERI2GACTALRM1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.rao.RaoE2GRActiveAlarm"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RAO Active Alarm 2G"));
		mapParser.put("RAOERI2G3GACTALRM1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.rao.RaoE2G3GActiveAlarm"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RAO Active Alarm 2G-3G Sumbagteng"));
		mapParser.put("RAOERI2GLOGALRM1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.rao.RaoE2GRLogAlarm"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RAO Log Alarm 2G"));
		mapParser.put("RAOERI3GACTALRM1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.rao.RaoE3GRActiveAlarm"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RAO Active Alarm 2G"));
		mapParser.put("RAOERI3GLOGALRM1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.rao.RaoE3GRLogAlarm"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RAO Active Alarm 2G"));
		mapParser.put("OSSDASH-01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.oss.OssDashboardZte10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"OSS Dashboard"));
		mapParser.put("NIMFAR1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.nim.FarParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"RAO Active Alarm 2G"));
		mapParser.put("CMSPFMHUA1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.dashboard.cmstransport.HuaweiTransportAscii10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"CMS Huawei PFM Transport 1.0"));		
		
		//cli-mml
		mapParser.put("HUAMMLTASK01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cli.huawei.sgsn.HuaweiMmlTasParserV10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Huawei HLR MMLTask v.1.0"));
		mapParser.put("CLIMSCNOK1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cli.nokia.NokiaCliParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Nokia CLI Txt Mss v.1.0"));
		mapParser.put("CLIMSCERI1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cli.ericsson.EricssonCliMscParserV10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CLI Txt Mss v.1.0"));
		mapParser.put("CLISGSNERI1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cli.ericsson.EricssonCliSgsnParserV10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson CLI Txt Sgsn v.1.0"));
		mapParser.put("IOSTAT.1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.util.IostatIopsParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"IOSTAT.1.0"));
		
		//LTE
		mapParser.put("LTEHUARADIO.1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.lte.pm.huawei.HuaweiLteRadio10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"LTEHUARADIO.1.0"));
		mapParser.put("LTEHUARADIO.2.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.lte.pm.huawei.HuaweiLTEXmlParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"LTEHUARADIO XML"));
		mapParser.put("LTENOKRADIO.1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.lte.pm.nokia.NokiaLTEXmlParser11"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"LTENOKRADIO.1.0"));
		mapParser.put("LTENOKDocMappingReader1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.lte.pm.nokia.NokiaLTEPdfDocsMappingReader"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"LTENOKDocMappingReader1.0"));
		mapParser.put("LTEERI1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.lte.pm.ericsson.EricssonPMLTEEriV10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Ericsson LTE, rln buffering, timediff hour"));
				
		//Traversa
		mapParser.put("TRAVERSA.R3.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.traversa.TraversaParser"),
				Class.forName("id.co.telkom.parser.entity.traversa.TraversaCommonInitiator"),
				"Traversa Release.3.0"));
		
		
		//--- TELKOMCEL ------
		//IMOC
		mapParser.put("IMOC-XLSX-SUB.V1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.imoc.ImocParserXlsSubs"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"IMOC-XLSX-SUB"));
		mapParser.put("IMOC-XLSX.V2.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.imoc.ImocParserXls"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"IMOC-XLSX-SUB"));
		mapParser.put("IMOC-XLSX-BTSPERF.V1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.imoc.ImocParserXlsBTSPerformance"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"IMOC-XLSX-BTSPERF"));
		mapParser.put("IMOC-XLSX-BTSPERF.V2.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.imoc.ImocParserXlsBTSPerformanceV2"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"IMOC-XLSX-BTSPERF"));
		//Digisquare
		mapParser.put("DIGI-PE-INET.V1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.digisquare.DigisquarePEInetParser10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"Digisquare PE-INET Parser"));
		//TLR Sms Router
		mapParser.put("TLR-CDRV1.0", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.tlr.TelkomcelTLRParser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TLR-CDR"));
		//SAP Telkom
		mapParser.put("SapAllitemReaderV01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.sap.SapAllitemReaderV01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"SapAllitemReaderV01"));
		mapParser.put("SapARReaderV01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.sap.SapARReaderV01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"SapARReaderV01"));
		mapParser.put("SapClearedReaderV01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.sap.SapClearedReaderV01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"SapClearedReaderV01"));
		mapParser.put("SapChilliReaderV01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.sap.SapChilliReaderV01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"SapChilliReaderV01"));
		//Network Performance
		mapParser.put("TcelNetworkPerfExcel01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.TcelNetworkPerfExcel01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TcelNetworkPerfExcel01"));
		mapParser.put("TcelNetworkPerfExcel02", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.TcelNetworkPerfExcel02"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TcelNetworkPerfExcel02"));
		
		//Occupancy
		mapParser.put("TcelOccupancyParser01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.TcelOccupancyParser01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TcelOccupancyParser01"));
		//Hubbing-Interkoneksi
		mapParser.put("TcelVotlvcdr01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.hubbing.Votlvcdr01"),
				Class.forName("id.co.telkom.parser.entity.pm.tcel.hubbing.VotlvcdrInitiator"),
				"TcelVotlvcdr01"));
		mapParser.put("TcelSmtlvcdr01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.hubbing.Smtlvcdr01"),
				Class.forName("id.co.telkom.parser.entity.pm.tcel.hubbing.VotlvcdrInitiator"),
				"TcelVotlvcdr01"));
		mapParser.put("SyniverseRoamingReport", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.SyniverseRoamingReport"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"SyniverseRoamingReport"));
		mapParser.put("SyniverseRoamingReportV2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.SyniverseRoamingReportV2"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"SyniverseRoamingReportV2"));
		mapParser.put("InternationalInterconnectReport", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.InternationalInterconnectReport"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"InternationalInterconnectReport"));
		//BonitaPredifineLogs01
		mapParser.put("BonitaPredifineLogs01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.BonitaXlsPredifineLogs"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"BonitaPredifineLogs01"));
		//TcelPayroll
		mapParser.put("TcelPayroll", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.TcelPayroll"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TcelPayroll"));
		
		//ActiveAlarmEid
		mapParser.put("ActiveAlarmEid", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.BscAlarmA1Parser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"ActiveAlarmEid"));
		mapParser.put("ActiveAlarmEidA2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.BscAlarmA2Parser"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"ActiveAlarmEidA2"));
		//Hourly CRS
		mapParser.put("HourlyCrsV1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.HourlyPerformanceCrs"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"HourlyCrsV1"));
		
		//PE Inet (Ping)
		mapParser.put("PEInetV11", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.cli.peinet.CliPEInetParserV10"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"PEInetV11"));
		
		//UNTL Students
		mapParser.put("UntlStudents", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tcel.UntlStudents"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"UntlStudents"));
		
		//Telkom MTTR
		mapParser.put("TelkomMttr01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.telkom.TelkomDwsMttr01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TelkomMttr"));
		
		//Telkomsel Dashboard Transport
		mapParser.put("TselTransportXls01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tseltransport.TselNetworkPerfExcel01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TselTransportXls01"));
		mapParser.put("TselLinkCsv01", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.tseltransport.TselLinkCsvReader01"),
				Class.forName("id.co.telkom.parser.ParserStandardInitiator"),
				"TselLinkCsv01"));
		
		//--- TELKOM ------
		//HAUD CDR
		mapParser.put("HaudCDRV1", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.telkom.TelkomHaudCDRv1"),
				Class.forName("id.co.telkom.parser.entity.pm.telkom.TelkomHaudInitiatiorV1"),
				"HaudCDRV1"));
		
		mapParser.put("HaudCDRV2", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.telkom.TelkomHaudCDRv1"),
				Class.forName("id.co.telkom.parser.entity.pm.telkom.TelkomHaudInitiatiorV2"),
				"HaudCDRV2"));
		
		mapParser.put("HaudCDRV3", new ParserObject(
				Class.forName("id.co.telkom.parser.entity.pm.telkom.TelkomHaudCDRv3"),
				Class.forName("id.co.telkom.parser.entity.pm.telkom.TelkomHaudInitiatiorV1"),
				"HaudCDRV3"));
		
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Map<String, ParserObject> getMapParser() {
		return mapParser;
	}

	public Constructor<?> getParserContructor(ParserPropReader parserProp, Object[] paramConstructor){
		
		@SuppressWarnings("rawtypes")
		Class cynapsePath=mapParser.get(parserProp.getPARSER_ID()).getParser();
		if(cynapsePath!=null){
			try {
				java.lang.reflect.Constructor<?> cs[] = cynapsePath.getConstructors();
				java.lang.reflect.Constructor<?> c = null; 
		          for(int i=0;i < cs.length; i++) {  
		              if(paramConstructor.length == (cs[i]).getParameterTypes().length) {
		                  c = cs[i];
		                  break;
		              }
		          }
		          return c;
		          
			} catch (SecurityException e) {
				e.printStackTrace();
			} 
			
		}else{
			logger.error("Can't find parser Specified: "+parserProp.getPARSER_ID());
			System.err.println("Can't find parser Specified: "+parserProp.getPARSER_ID());
			System.exit(1);
		}	
		
		return null;
		
	}
	public Constructor<?> getParserInitContructor(ParserPropReader parserProp, Object[] paramConstructor){
		@SuppressWarnings("rawtypes")
		Class cynapsePath = mapParser.get(parserProp.getPARSER_ID()).getInitiator();
		if(cynapsePath!=null){
			try {
				java.lang.reflect.Constructor<?> cs[] = cynapsePath.getConstructors();
				java.lang.reflect.Constructor<?> c = null; 
		          for(int i=0;i < cs.length; i++) {  
		              if(paramConstructor.length == (cs[i]).getParameterTypes().length) {
		                  c = cs[i];
		                  break;
		              }
		          }
		          return c;
		          
			} catch (SecurityException e) {
				logger.error("Security Exception "+e);
				e.printStackTrace();
			} 
			
		}else{
			logger.error("Can't find parser Initial Specified: "+parserProp.getPARSER_ID());
			System.out.println("Can't find parser Initial Specified: "+parserProp.getPARSER_ID());
			System.exit(1);
		}
		
		return null;
		
	}
	
	public Constructor<?> getParserInitContructorOld(ParserPropReader parserProp, Object[] paramConstructor){
		
		String cynapsePath = mapParser.get(parserProp.getPARSER_ID()).getInitiator().toString();
		if(cynapsePath!=null){
			try {
				java.lang.reflect.Constructor<?> cs[] = Class.forName(cynapsePath).getConstructors();
				java.lang.reflect.Constructor<?> c = null; 
		          for(int i=0;i < cs.length; i++) {  
		              if(paramConstructor.length == (cs[i]).getParameterTypes().length) {
		                  c = cs[i];
		                  break;
		              }
		          }
		          return c;
		          
			} catch (SecurityException e) {
				logger.error("Security Exception "+e);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				logger.error("Class Not Found For "+parserProp.getPARSER_ID());
				System.err.println("Class Not Found For "+parserProp.getPARSER_ID());
				System.exit(1);
			} 
			
		}else{
			logger.error("Can't find parser Initial Specified: "+parserProp.getPARSER_ID());
			System.out.println("Can't find parser Initial Specified: "+parserProp.getPARSER_ID());
			System.exit(1);
		}	
		
		return null;
		
	}
	
}

