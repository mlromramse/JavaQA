package org.jblooming.waf.html.input;

import org.jblooming.utilities.CodeValueList;
import org.jblooming.waf.SessionState;
import org.jblooming.waf.view.PageSeed;

/**
 * @author Pietro Polsinelli : ppolsinelli@open-lab.com
 */
public class CountryCombo extends Combo {


  public CountryCombo(String fieldName,
                      String separator,
                      String htmlClass,
                      int maxLenght,
                      boolean translateValues,
                      String script,
                      PageSeed currentUrl,
                      SessionState sess) {


    super(null, fieldName,
            separator,
            htmlClass,
            maxLenght,
            null,
            CountryCombo.getCountryValueList(),
            script);

  }

  /**
   * This builds label from fieldName and view's client entries using i18n.
   */
  public CountryCombo(String fieldName,
                      String separator,
                      String htmlClass,
                      int maxLenght,
                      String initialSelectedCode,

                      boolean translateValues,
                      String script,
                      PageSeed currentUrl, SessionState sess) {

    super(fieldName,
            fieldName,
            separator,
            htmlClass,
            maxLenght,
            initialSelectedCode,
            CountryCombo.getCountryValueList(),
            script);
  }


  public CountryCombo(String label,
                      String fieldName,
                      String separator,
                      String htmlClass,
                      int maxLenght,
                      String initialSelectedCode,
                      boolean translateValues,
                      String script,
                      PageSeed currentUrl, SessionState sess) {

    super(label,
            fieldName,
            separator,
            htmlClass,
            maxLenght,
            initialSelectedCode,
            CountryCombo.getCountryValueList(),
            script);
  }


  public static CodeValueList getCountryValueList() {

    CodeValueList cvl = new CodeValueList();
    cvl.add("AFG");
    cvl.add("ALB");
    cvl.add("DZA");
    cvl.add("ASM");
    cvl.add("AND");
    cvl.add("AGO");
    cvl.add("AIA");
    cvl.add("ATA");
    cvl.add("ATG");
    cvl.add("ARG");
    cvl.add("ARM");
    cvl.add("ABW");
    cvl.add("AUS");
    cvl.add("AUT");
    cvl.add("AZE");
    cvl.add("BHS");
    cvl.add("BHR");
    cvl.add("BGD");
    cvl.add("BRB");
    cvl.add("BLR");
    cvl.add("BEL");
    cvl.add("BLZ");
    cvl.add("BEN");
    cvl.add("BMU");
    cvl.add("BTN");
    cvl.add("BOL");
    cvl.add("BIH");
    cvl.add("BWA");
    cvl.add("BVT");
    cvl.add("BRA");
    cvl.add("IOT");
    cvl.add("BRN");
    cvl.add("BGR");
    cvl.add("BFA");
    cvl.add("BDI");
    cvl.add("KHM");
    cvl.add("CMR");
    cvl.add("CAN");
    cvl.add("CPV");
    cvl.add("CYM");
    cvl.add("CAF");
    cvl.add("TCD");
    cvl.add("CHL");
    cvl.add("CHN");
    cvl.add("CXR");
    cvl.add("CCK");
    cvl.add("COL");
    cvl.add("COL");
    cvl.add("COM");
    cvl.add("COG");
    cvl.add("COD");
    cvl.add("COK");
    cvl.add("CRI");
    cvl.add("CIV");
    cvl.add("HRV");
    cvl.add("CUB");
    cvl.add("CYP");
    cvl.add("CZE");
    cvl.add("DNK");
    cvl.add("DJI");
    cvl.add("DMA");
    cvl.add("DOM");
    cvl.add("ECU");
    cvl.add("EGY");
    cvl.add("SLV");
    cvl.add("GNQ");
    cvl.add("ERI");
    cvl.add("EST");
    cvl.add("ETH");
    cvl.add("FRO");
    cvl.add("FLK");
    cvl.add("FJI");
    cvl.add("FIN");
    cvl.add("FRA");
    cvl.add("GUF");
    cvl.add("PYF");
    cvl.add("ATF");
    cvl.add("GAB");
    cvl.add("GMB");
    cvl.add("GEO");
    cvl.add("DEU");
    cvl.add("GHA");
    cvl.add("GIB");
    cvl.add("GRC");
    cvl.add("GRL");
    cvl.add("GRD");
    cvl.add("GLP");
    cvl.add("GUM");
    cvl.add("GTM");
    cvl.add("GIN");
    cvl.add("GNB");
    cvl.add("GUY");
    cvl.add("HTI");
    cvl.add("HMD");
    cvl.add("HND");
    cvl.add("HKG");
    cvl.add("HUN");
    cvl.add("ISL");
    cvl.add("IND");
    cvl.add("IDN");
    cvl.add("IRN");
    cvl.add("IRQ");
    cvl.add("IRL");
    cvl.add("ISR");
    cvl.add("ITA");
    cvl.add("JAM");
    cvl.add("JPN");
    cvl.add("JOR");
    cvl.add("KAZ");
    cvl.add("KEN");
    cvl.add("KIR");
    cvl.add("PRK");
    cvl.add("KOR");
    cvl.add("KWT");
    cvl.add("KGZ");
    cvl.add("LAO");
    cvl.add("LVA");
    cvl.add("LBN");
    cvl.add("LSO");
    cvl.add("LBR");
    cvl.add("LBY");
    cvl.add("LIE");
    cvl.add("LTU");
    cvl.add("LUX");
    cvl.add("MAC");
    cvl.add("MKD");
    cvl.add("MDG");
    cvl.add("MWI");
    cvl.add("MYS");
    cvl.add("MDV");
    cvl.add("MLI");
    cvl.add("MLT");
    cvl.add("MHL");
    cvl.add("MTQ");
    cvl.add("MRT");
    cvl.add("MUS");
    cvl.add("MYT");
    cvl.add("MEX");
    cvl.add("FSM");
    cvl.add("MDA");
    cvl.add("MCO");
    cvl.add("MNG");
    cvl.add("MSR");
    cvl.add("MAR");
    cvl.add("MOZ");
    cvl.add("MMR");
    cvl.add("NAM");
    cvl.add("NRU");
    cvl.add("NPL");
    cvl.add("NLD");
    cvl.add("ANT");
    cvl.add("NCL");
    cvl.add("NZL");
    cvl.add("NIC");
    cvl.add("NER");
    cvl.add("NGA");
    cvl.add("NIU");
    cvl.add("NFK");
    cvl.add("MNP");
    cvl.add("NOR");
    cvl.add("OMN");
    cvl.add("PAK");
    cvl.add("PLW");
    cvl.add("PSE");
    cvl.add("PAN");
    cvl.add("PNG");
    cvl.add("PRY");
    cvl.add("PER");
    cvl.add("PHL");
    cvl.add("PCN");
    cvl.add("POL");
    cvl.add("PRT");
    cvl.add("PRI");
    cvl.add("QAT");
    cvl.add("REU");
    cvl.add("ROU");
    cvl.add("RUS");
    cvl.add("RWA");
    cvl.add("SHN");
    cvl.add("KNA");
    cvl.add("LCA");
    cvl.add("SPM");
    cvl.add("VCT");
    cvl.add("WSM");
    cvl.add("SMR");
    cvl.add("STP");
    cvl.add("SAU");
    cvl.add("SEN");
    cvl.add("SYC");
    cvl.add("SLE");
    cvl.add("SGP");
    cvl.add("SVK");
    cvl.add("SVN");
    cvl.add("SLB");
    cvl.add("SOM");
    cvl.add("ZAF");
    cvl.add("SGS");
    cvl.add("ESP");
    cvl.add("LKA");
    cvl.add("SDN");
    cvl.add("SUR");
    cvl.add("SJM");
    cvl.add("SWZ");
    cvl.add("SWE");
    cvl.add("CHE");
    cvl.add("SYR");
    cvl.add("TWN");
    cvl.add("TJK");
    cvl.add("TZA");
    cvl.add("THA");
    cvl.add("TLS");
    cvl.add("TGO");
    cvl.add("TKL");
    cvl.add("TON");
    cvl.add("TTO");
    cvl.add("TUN");
    cvl.add("TUR");
    cvl.add("TKM");
    cvl.add("TCA");
    cvl.add("TUV");
    cvl.add("UGA");
    cvl.add("UKR");
    cvl.add("ARE");
    cvl.add("GBR");
    cvl.add("USA");
    cvl.add("UMI");
    cvl.add("URY");
    cvl.add("UZB");
    cvl.add("VUT");
    cvl.add("VAT");
    cvl.add("VEN");
    cvl.add("VNM");
    cvl.add("VGB");
    cvl.add("VIR");
    cvl.add("WLF");
    cvl.add("ESH");
    cvl.add("YEM");
    cvl.add("YUG");
    cvl.add("ZMB");
    cvl.add("ZWE");
    return cvl;
  }

}