package com.pontusvision.gdpr;

import com.pontusvision.graphutils.PontusJ2ReportingFunctions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(AnnotationTestsOrderer.class)
@TestClassesOrder(4)

public class PVJinJavaTests extends AppTest {


  @Test
  public void test00001DateLocaleFormat() throws InterruptedException {
    try {

//    Country and Lang codes at src/test/resources/country_date_formats.csv

      String brazilianDate = PontusJ2ReportingFunctions
              .dateLocaleFormat("Sat Jan 08 08:00:00 UTC 2022", "pt", "BR");
      assertEquals("8 de Janeiro de 2022", brazilianDate);

      String chineseDate = PontusJ2ReportingFunctions
              .dateLocaleFormat("Wed Oct 6 10:15:09 UTC 2021", "zh", "CN");
      assertEquals("2021年10月6日", chineseDate);

//      String frenchDate = PontusJ2ReportingFunctions
//              .dateLocaleFormat("Sat Dec 28 12:00:49 UTC 2019", "fr", "FR");
//      assertEquals("28 décembre 2019", frenchDate);

      String hindiDate = PontusJ2ReportingFunctions
              .dateLocaleFormat("Mon Jul 20 19:56:34 UTC 2020", "hi", "IN");
      assertEquals("२० जुलाई, २०२०", hindiDate);

      String lebaneseDate = PontusJ2ReportingFunctions
              .dateLocaleFormat("Tue Mar 01 05:29:18 UTC 2022", "ar", "LB");
      assertEquals("01 آذار, 2022", lebaneseDate);

//      String usaDate = PontusJ2ReportingFunctions
//              .dateLocaleFormat("Sat Dec 28 15:57:33 UTC 2019", "en", "US");
//      assertEquals("December 28, 2019", usaDate);

    } catch (Exception e) {
      e.printStackTrace();
      assertNull(e);

    }

  }

//  @Test
//  public void test00002FormatDateNow() throws InterruptedException {
//    try {
//
////    Country and Lang codes at src/test/resources/country_date_formats.csv
//
//      String currentMonthInPortuguese = PontusJ2ReportingFunctions
//              .formatDateNow("MMMM");
//      System.out.println(currentMonthInPortuguese);
//
//      String currentDateInChinese = PontusJ2ReportingFunctions
//              .formatDateNow("dd MMMM YYYY", "zh", "CN");
//      System.out.println(currentDateInChinese);
//
//      String frenchDate = PontusJ2ReportingFunctions
//              .formatDateNow("Sat Dec 28 12:00:49 UTC 2019", "fr", "FR");
//      assertSame("28 décembre 2019", frenchDate);
//
//      String hindiDate = PontusJ2ReportingFunctions
//              .formatDateNow("Mon Jul 20 19:56:34 UTC 2020", "hi", "IN");
//      assertSame("२० जुलाई, २०२०", hindiDate);
//
//      String lebaneseDate = PontusJ2ReportingFunctions
//              .formatDateNow("Tue Mar 01 05:29:18 UTC 2022", "ar", "LB");
//      assertSame("01 آذار, 2022", lebaneseDate);
//
//      String usaDate = PontusJ2ReportingFunctions
//              .formatDateNow("Sat Dec 28 15:57:33 UTC 2019", "en", "US");
//      assertSame("December 28, 2019", usaDate);
//
//    } catch (Exception e) {
//      e.printStackTrace();
//      assertNull(e);
//
//    }
//
//  }


//  @Test
//  public void test00003FormatLocaleDateNow() throws InterruptedException {
//    try {
//
//      //    Country and Lang codes at src/test/resources/country_date_formats.csv
//
//      String brazilianDate = PontusJ2ReportingFunctions
//              .formatLocaleDateNow("", "pt", "BR");
//      assertEquals("", brazilianDate);
//
////      String chineseDate = PontusJ2ReportingFunctions
////              .dateLocaleFormat("Wed Oct 6 10:15:09 UTC 2021", "zh", "CN");
////      assertEquals("2021年10月6日", chineseDate);
////
////      String frenchDate = PontusJ2ReportingFunctions
////              .dateLocaleFormat("Sat Dec 28 12:00:49 UTC 2019", "fr", "FR");
////      assertEquals("28 décembre 2019", frenchDate);
////
////      String hindiDate = PontusJ2ReportingFunctions
////              .dateLocaleFormat("Mon Jul 20 19:56:34 UTC 2020", "hi", "IN");
////      assertEquals("२० जुलाई, २०२०", hindiDate);
////
////      String lebaneseDate = PontusJ2ReportingFunctions
////              .dateLocaleFormat("Tue Mar 01 05:29:18 UTC 2022", "ar", "LB");
////      assertEquals("01 آذار, 2022", lebaneseDate);
////
////      String usaDate = PontusJ2ReportingFunctions
////              .dateLocaleFormat("Sat Dec 28 15:57:33 UTC 2019", "en", "US");
////      assertEquals("December 28, 2019", usaDate);
//
//    } catch (Exception e) {
//      e.printStackTrace();
//      assertNull(e);
//
//    }
//
//  }

}
