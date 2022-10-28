package com.pontusvision.utils;

public class SharepointUserRef extends SharepointRef {
// TODO: Convert this {LookupId=2, LookupValue=CRM-Contatos} to a class.

  public static SharepointUserRef fromString(String str){
    SharepointUserRef retVal = new SharepointUserRef();
    retVal.LookupId= Integer.parseInt(getStrVal(str,"LookupId"));
    retVal.LookupValue = getStrVal(str,"LookupValue");
    retVal.Email = getStrVal(str,"Email");
    return retVal;
  }

  String Email;

  public String getEmail() {
    return Email;
  }

  public void setEmail(String email) {
    Email = email;
  }
}
