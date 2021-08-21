package com.pontusvision.ingestion;

/*
Copyright Leo Martins -
Pontus Vision - 2010-2019
*/

import java.util.Map;

//@Tags({ "pontus", "StringReplacer"})
//@CapabilityDescription("Fast String pattern replacement library.")
public class StringReplacer {

  //  public static final Configuration STRICT_PROVIDER_CONFIGURATION = Configuration.builder().jsonProvider(new JacksonJsonProvider()).build();
  //  public static final JsonProvider JSON_PROVIDER = STRICT_PROVIDER_CONFIGURATION.jsonProvider();
  private static final String cronRegex = null;

  public static String greatestCommonPrefix(String a, String b) {
    if (a == null && b == null) {
      return "";
    }

    if (a.equals(b)) {
      return a;
    }
    int minLength = Math.min(a.length(), b.length());
    for (int i = 0; i < minLength; i++) {
      if (a.charAt(i) != b.charAt(i)) {
        return a.substring(0, i);
      }
    }
    return a.substring(0, minLength);
  }

  public static String replaceFirst(final String str, final String searchChars, String replaceChars) {
    if ("".equals(str) || "".equals(searchChars) || searchChars.equals(replaceChars)) {
      return str;
    }
    if (replaceChars == null) {
      replaceChars = "";
    }
    final int strLength = str.length();
    final int searchCharsLength = searchChars.length();
    StringBuilder buf = new StringBuilder(str);
    boolean modified = false;
    int start = 0;
    for (int i = 0; i < strLength; i++) {
      start = buf.indexOf(searchChars, i);

      if (start == -1) {
        if (i == 0) {
          return str;
        }
        return buf.toString();
      }
      buf = buf.replace(start, start + searchCharsLength, replaceChars);
      modified = true;
      return buf.toString();

    }
    if (!modified) {
      return str;
    } else {
      return buf.toString();
    }
  }

  public static String replaceAll(final String str, final String searchChars, String replaceChars) {
    if ("".equals(str) || "".equals(searchChars) || searchChars.equals(replaceChars)) {
      return str;
    }
    if (replaceChars == null) {
      replaceChars = "";
    }
    final int strLength = str.length();
    final int searchCharsLength = searchChars.length();
    StringBuilder buf = new StringBuilder(str);
    boolean modified = false;
    int start = 0;
    for (int i = 0; i < strLength; i++) {
      start = buf.indexOf(searchChars, i);

      if (start == -1) {
        if (i == 0) {
          return str;
        }
        return buf.toString();
      }
      buf = buf.replace(start, start + searchCharsLength, replaceChars);
      modified = true;

    }
    if (!modified) {
      return str;
    } else {
      return buf.toString();
    }
  }


}
