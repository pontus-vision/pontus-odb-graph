package com.pontusvision.utils;

public class BudibaseRef {
// TODO: Convert this {LookupId=2, LookupValue=CRM-Contatos} to a class.
  public static String getStrVal (String str, String varName){
    int startofLookupId = str.indexOf(varName) ;
    int endOfLookupId = startofLookupId + varName.length();
    String startOfLookupIdVal = str.substring(endOfLookupId+1);
    String lookupIdVal = startOfLookupIdVal.split(",|}")[0];
    return lookupIdVal;
  }
  public static BudibaseRef fromString(String str){
    BudibaseRef retVal = new BudibaseRef();
    retVal.LookupId= getStrVal(str,"_id");
    retVal.LookupValue = getStrVal(str,"primaryDisplay");
    return retVal;
  }
  String LookupId;
  String LookupValue;

  public String getLookupId() {
    return LookupId;
  }

  public void setLookupId(String lookupId) {
    LookupId = lookupId;
  }

  public String getLookupValue() {
    return LookupValue;
  }

  public void setLookupValue(String lookupValue) {
    LookupValue = lookupValue;
  }
}
