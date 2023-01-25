package com.pontusvision.utils;

public class WebinyRefCpf {
  public static String getStrVal (String str, String varName){
    int start = str.indexOf(varName) ;
    int end = start + varName.length();
    String startCpf = str.substring(end+1);
    return startCpf.split("[,}]")[0];
  }
  public static WebinyRefCpf fromString(String str){
    WebinyRefCpf retVal = new WebinyRefCpf();
    retVal.titularesCpfDoTitular = getStrVal(str,"titularesCpfDoTitular");
    return retVal;
  }
  String titularesCpfDoTitular;

  public String getTitularesCpfDoTitular() {
    return titularesCpfDoTitular;
  }

  public void setTitularesCpfDoTitular(String titularesCpfDoTitular) {
    this.titularesCpfDoTitular = titularesCpfDoTitular;
  }
}
