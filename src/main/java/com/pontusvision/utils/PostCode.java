package com.pontusvision.utils;

public class PostCode
{

  private static java.lang.String regex = "^(([A-Z][A-HJ-Y]?\\d[A-Z\\d]?|ASCN|STHL|TDCU|BBND|[BFS]IQQ|PCRN|TKCA) ?\\d[A-Z]{2}|BFPO ?\\d{1,4}|(KY\\d|MSR|VG|AI)[ -]?\\d{4}|[A-Z]{2} ?\\d{2}|GE ?CX|GIR ?0A{2}|SAN ?TA1)$";
  private static java.util.regex.Pattern pattern = java.util.regex.Pattern
      .compile(regex, java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.CASE_INSENSITIVE);
  private java.lang.String postCodeFormatted;

  public PostCode(java.lang.String postCodeRaw)
  {
    postCodeFormatted = format(postCodeRaw);
  }

  public static java.lang.String format(java.lang.String raw)
  {
    if (raw == null){
      return null;
    }
    java.lang.String rawLowerCase = raw.toLowerCase().replaceAll("\\s","");
    final java.util.regex.Matcher matcher = pattern.matcher(rawLowerCase);

    java.lang.StringBuffer retVal = new java.lang.StringBuffer();
    while (matcher.find())
    {
      java.lang.String fullPostCode = matcher.group(0);
      int ilen = matcher.groupCount();
      java.lang.String firstHalf = fullPostCode;
      for (int i = 1; i <= ilen; i++)
      {
        java.lang.String currStr = matcher.group(i);
        if (currStr != null && !currStr.equals(fullPostCode))
        {
          firstHalf = currStr;
        }

      }

      firstHalf = firstHalf.trim();
      java.lang.String secondHalf = org.codehaus.groovy.runtime.StringGroovyMethods
          .minus(fullPostCode, firstHalf);// = fullPostCode.split(groupFound)
      secondHalf = secondHalf.trim();

      retVal.append(firstHalf).append(" ").append(secondHalf);
      //            sb?.append("\n${label}:${firstHalf} ${secondHalf}")
    }

    if (org.codehaus.groovy.runtime.StringGroovyMethods.size(retVal) == 0)
    {
      retVal.append(rawLowerCase);
    }

    return retVal.toString();

  }


  public java.lang.String toString()
  {
    return postCodeFormatted;
  }



}
