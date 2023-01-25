package com.pontusvision.utils;

public class WebinyRefLawfulBasis {
  public static String getStrVal (String str, String varName){
    int start = str.indexOf(varName) ;
    int end = start + varName.length();
    String startLawfulBasis = str.substring(end+1);
    return startLawfulBasis.split("[,}]")[0];
  }
  public static WebinyRefLawfulBasis fromString(String str){
    WebinyRefLawfulBasis retVal = new WebinyRefLawfulBasis();
    retVal.basesLegaisReferencia = getStrVal(str,"basesLegaisReferencia");
    return retVal;
  }
  String basesLegaisReferencia;

  public String getBasesLegaisReferencia() {
    return basesLegaisReferencia.trim().toUpperCase();
  }

  public void setBasesLegaisReferencia(String basesLegaisReferencia) {
    this.basesLegaisReferencia = basesLegaisReferencia;
  }
}
