package com.pontusvision.utils;

import com.google.gson.Gson;
import org.json.JSONArray;

import java.util.*;
import java.util.stream.Stream;

public class NLPCleaner {
  public static Gson gson = new Gson();

  public static boolean filterPassFunc(String s, Set<String> filterList) {
    if (filterList != null) {
      return s != null && s.length() > 2 && !org.apache.commons.lang.StringUtils.isNumeric(s) && !filterList.contains(s);
    }

    return s != null && s.length() > 2;
  }


  public static String filter(Collection<String> listOfNames) {
    return filter(listOfNames, true, null);
  }

  public static String filter(String jsonArrayStr) {

    return filter(gson.fromJson(jsonArrayStr, ArrayList.class), true, null);


  }


  public static String filter(String jsonArrayStr, boolean splitSpaces, Set<String> filterList) {

    return filter(gson.fromJson(jsonArrayStr, ArrayList.class), splitSpaces, filterList);


  }

  public static String filter(Collection<String> listOfNames, boolean splitSpaces, Set<String> filterList) {

    Set<String> retVal = new HashSet<>();

    Stream<String> stringStream =
      listOfNames.parallelStream().filter(s -> filterPassFunc(s, filterList));

    stringStream.forEach(s -> {
      if (splitSpaces) {
        String[] parts = s.split("\\s");
        for (int i = 0, ilen = parts.length; i < ilen; i++) {
          String part = parts[i];
          if (filterPassFunc(part, filterList)) {
            retVal.add(part.trim());
          }
        }
      }
      retVal.add(s.trim());
    });

    stringStream.close();
    JSONArray array = new JSONArray(retVal);

    return array.toString();

  }

  public static String normalizeName(String str) {
    if (str == null) {
      return str;
    }
    String retVal = str.trim().toUpperCase();
    retVal = java.text.Normalizer.normalize(retVal, java.text.Normalizer.Form.NFD);
    retVal = retVal.replaceAll("\\p{M}", "");
    retVal = retVal.replaceAll("[^\\p{ASCII}]", "");
    return retVal;
  }

  public static void main(String[] args) {
    System.out.println(filter(Arrays.asList(args)));
  }
}
