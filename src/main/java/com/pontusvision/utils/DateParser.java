package com.pontusvision.utils;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateParser {
  public static Parser parser = new Parser();

  public static Date parseDate(String self){
    List<DateGroup> dateGroup = parser.parse(self);
    Date retVal = null;

    if (!dateGroup.isEmpty()) {
      DateGroup dg = dateGroup.get(0);

      boolean isTimeInferred = dg.isTimeInferred();

      List<Date> dates = dg.getDates();

      for (int i = 0, iLen = dates.size() ; i < iLen; i++){
        Date it = dates.get(i);
        retVal = it;
        if (isTimeInferred) {
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(retVal);
          calendar.set(Calendar.HOUR_OF_DAY, 1);
          calendar.set(Calendar.MINUTE, 1);
          calendar.set(Calendar.SECOND, 1);
          calendar.set(Calendar.MILLISECOND, 0);
          retVal = calendar.getTime();

        }
      }

    }
    return retVal;
  }

}
