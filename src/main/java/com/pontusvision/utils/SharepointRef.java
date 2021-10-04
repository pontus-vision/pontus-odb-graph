package com.pontusvision.utils;

import jnr.ffi.annotations.In;

public class SharepointRef {
// TODO: Convert this {LookupId=2, LookupValue=CRM-Contatos} to a class.
  public static String getStrVal (String str, String varName){
    int startofLookupId = str.indexOf(varName) ;
    int endOfLookupId = startofLookupId + varName.length();
    String startOfLookupIdVal = str.substring(endOfLookupId+1);
    String lookupIdVal = startOfLookupIdVal.split(",|}")[0];
    return lookupIdVal;
  }
  public static SharepointRef fromString(String str){
    SharepointRef retVal = new SharepointRef();
    retVal.LookupId= Integer.parseInt(getStrVal(str,"LookupId"));
    retVal.LookupValue = getStrVal(str,"LookupValue");
    return retVal;
  }
  Integer LookupId;
  String LookupValue;

  public Integer getLookupId() {
    return LookupId;
  }

  public void setLookupId(Integer lookupId) {
    LookupId = lookupId;
  }

  public String getLookupValue() {
    return LookupValue;
  }

  public void setLookupValue(String lookupValue) {
    LookupValue = lookupValue;
  }
}
