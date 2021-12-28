package id.co.telkom.parser.entity.dashboard.nim;

import id.co.telkom.parser.common.model.ConfiguredHeader;

public class FarHeader {

	public static ConfiguredHeader[] siteReportHeader =  new ConfiguredHeader[]{
		new ConfiguredHeader("    NO    "),
		new ConfiguredHeader("SITE_ID   "),			
		new ConfiguredHeader("SITE_NAME                          "),
		new ConfiguredHeader("COORDINATE                     "),
		new ConfiguredHeader("ADDRESS                                                                  "),
		new ConfiguredHeader("CITY                              "),
		new ConfiguredHeader("CREATION_DATE   "),
		new ConfiguredHeader("ACTIVE_STATUS     "),
		new ConfiguredHeader("SITE_ID_INACTIVE_DATE          "),
		new ConfiguredHeader("SITE_ID_INACTIVE_REASON                         "),
		new ConfiguredHeader("REGISTER_STATUS   "),
		new ConfiguredHeader("SITE_TYPE               "),
		new ConfiguredHeader("REGION                   "),
		new ConfiguredHeader("LAND_STATUS              "),
		new ConfiguredHeader("TOWER_STATUS")
	}; 
	
	public static ConfiguredHeader[] categoryReportHeader =  new ConfiguredHeader[]{
		new ConfiguredHeader("Asset_Number  ".toUpperCase()),
		new ConfiguredHeader("Asset_Description                                                                                ".toUpperCase()),			
		new ConfiguredHeader("DATE_PLACED_INSERVICE","In Service  ".length()),
		new ConfiguredHeader("DEPRN_METHOD","Method  ".length()),
		new ConfiguredHeader("LIFE_YR_MO","  Yr.Mo         ".length()),
		new ConfiguredHeader("             COST          "),
		new ConfiguredHeader("DEPRCATION_AMOUNT","          Amount         ".length()),
		new ConfiguredHeader("YEAR_TO_DATE_DEPRICATION","     Depreciation         ".length()),
		new ConfiguredHeader("DEPRICATION_RESERVE","          Reserve".length()),
		
		new ConfiguredHeader(" Percent   ".toUpperCase()),
		new ConfiguredHeader("Expense_Account ".toUpperCase()),
		new ConfiguredHeader("Asset_Key            ".toUpperCase()),
		new ConfiguredHeader("Category                        ".toUpperCase()),
		new ConfiguredHeader("Location                                      ".toUpperCase()),
		new ConfiguredHeader("   Site_ID                                                     ".toUpperCase()),
		new ConfiguredHeader("                              Site_Name".toUpperCase())
		
	}; 
	
	
	public static ConfiguredHeader[] assetKeyReportHeader =  new ConfiguredHeader[]{
		new ConfiguredHeader("Cc     ".toUpperCase(),true),
		new ConfiguredHeader("Asset_Description                                                                                                                               ".toUpperCase()),			
		new ConfiguredHeader("DATE_PLACED_INSERVICE","In Service  ".length()),
		new ConfiguredHeader("DEPRN_METHOD","Method  ".length()),
		new ConfiguredHeader("LIFE_YR_MO","  Yr.Mo ".length()),
		new ConfiguredHeader("             Cost ".toUpperCase()),
		new ConfiguredHeader("DEPRCATION_AMOUNT","          Amount ".length()),
		new ConfiguredHeader("YEAR_TO_DATE_DEPRICATION","    Depreciation ".length()),
		new ConfiguredHeader("DEPRICATION_RESERVE","          Reserve".length()),
		
		new ConfiguredHeader(" Percent  ".toUpperCase()),
		new ConfiguredHeader("Expense_Account       ".toUpperCase()),
		new ConfiguredHeader("Asset_Key  ".toUpperCase())
		
	}; 
}
