package com.pontusvision.utils;

public class WebinyRefId {
  public static String getStrVal (String str, String varName){
    int start = str.indexOf(varName) ;
    int end = start + varName.length();
    String startId = str.substring(end+1);
    return startId.split("[,}]")[0];
  }
  public static WebinyRefId fromString(String str){
    WebinyRefId retVal = new WebinyRefId();
    retVal.Id = getStrVal(str,"Id");
    return retVal;
  }
  String Id;

  public String getId() {
    return Id;
  }

  public void setId(String id) {
    Id = id;
  }
}
