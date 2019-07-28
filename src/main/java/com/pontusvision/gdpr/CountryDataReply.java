package com.pontusvision.gdpr;

import java.util.Collections;
import java.util.HashMap;

public class CountryDataReply
{
  public HashMap<String, Long> getCountryData()
  {
    return countryData;
  }

  public void setCountryData(HashMap<String, Long> countryData)
  {
    this.countryData = countryData;
  }

  HashMap<String,Long> countryData = new HashMap<>( Collections.EMPTY_MAP);
}
