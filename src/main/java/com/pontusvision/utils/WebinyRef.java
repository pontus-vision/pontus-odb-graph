package com.pontusvision.utils;

public class WebinyRef {
  public static String getStrVal (String str, String varName){
    int start = str.indexOf(varName) ;
    int end = start + varName.length();
    String startId = str.substring(end+1);
    return startId.split("[,}]")[0];
  }
  public static String fromString(String str, String key){
    WebinyRef retVal = new WebinyRef();
    return getStrVal(str,key);
  }

  public static String fromStringTrimUC(String str, String key){
    WebinyRef retVal = new WebinyRef();
    return getStrVal(str,key).trim().toUpperCase();
  }

}