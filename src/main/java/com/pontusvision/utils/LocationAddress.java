package com.pontusvision.utils;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import com.pontusvision.jpostal.AddressExpander;
import com.pontusvision.jpostal.AddressParser;
import com.pontusvision.jpostal.ParsedComponent;

import java.util.*;

public class LocationAddress {
  public static final String ADDRESS_PARSER_DIR = "/opt/pontus-graphdb/graphdb-current/datadir/libpostal";
  public static final String ADDRESS_PARSER_DIR_OPT = "pg.jpostal.datadir";


  public static AddressParser parser = AddressParser.getInstanceDataDir(System.getProperty(ADDRESS_PARSER_DIR_OPT,ADDRESS_PARSER_DIR ));
  public static AddressExpander expander = AddressExpander.getInstanceDataDir(System.getProperty(ADDRESS_PARSER_DIR_OPT,ADDRESS_PARSER_DIR ));

  private Map<String, String> tokens = new HashMap<>();

  private LocationAddress() {
  }

  static int maxExpansions = Integer.parseInt((System.getenv("PV_LOCATION_MAX_EXPANSION") != null)?
      System.getenv("PV_LOCATION_MAX_EXPANSION"): "5000" );

  public static LocationAddress fromString(String strVal) {

    LocationAddress retVal = new LocationAddress();


    ParsedComponent[] res = parser.parseAddress(strVal);

    for (int i = 0,ilen = res.length; i < ilen; i++){
      ParsedComponent it = res[i];
      String label = it.getLabel();
      String value = it.getValue();

      Set<String> vals = new HashSet<>();//retVal.tokens.computeIfAbsent(label,  k -> new HashSet<>() );

      // sb?.append("\n$label = $value")

      vals.add(value);

      String[] expansions = expander.expandAddress(value);

      for (int j = 0, jlen = expansions.length; j < jlen; j++){


        if (! "postcode".equals(label)) {

          vals.add(PostCode.format(expansions[j]));

        } else {

          vals.add(expansions[j]);


        }

      }
      TreeSet<String> sortedVals = new TreeSet<>(vals);
      String sortValsStr = sortedVals.toString();
      retVal.tokens.put(label,sortValsStr.substring(0,Math.min(maxExpansions, sortValsStr.length())));
    }
    return retVal;

  }


  public GraphTraversal addPropsToGraphTraverser(GraphTraversal g, String propPrefix) {
    Set<Map.Entry<String, String>> tokensEntrySet = tokens.entrySet();

    GraphTraversal retVal = g;
    for (Map.Entry<String, String> tokenEntry: tokensEntrySet)
    {
      StringBuffer propName = new StringBuffer(propPrefix).append(tokenEntry.getKey());
      retVal = retVal.property(propName, tokenEntry.getValue());
//      for (String val:tokenEntry.getValue())
//      {
//        retVal = retVal.property(propName, val);
//
//      }
    }

    return retVal;
  }


  public  Map<String, String> getTokens() {
    return tokens;//.asImmutable()
  }
  public String getVals(String key) {
    return tokens.get(key);
  }

  public String toString() {
    return tokens.toString();
  }

  public static String formatAddress(String address, String number, String complement, String disctrict, String city, String country, String postCode) {

    StringBuilder sb = new StringBuilder();

    boolean addressBool = address != null && !address.isEmpty();

    if (addressBool) {
      sb.append(address.trim().toUpperCase());
    }

    boolean numberBool = number != null && !number.isEmpty();

    if (numberBool && addressBool) {
      sb.append(" ").append(number.trim());
    } else if (numberBool && !addressBool) {
      sb.append(number.trim());
    } else {
      sb.append(" ").append("S/N");
    }

    boolean complementBool = complement != null && !complement.isEmpty();

    if (complementBool) {
      sb.append(" ").append(complement.trim().toUpperCase());
    }

    boolean disctrictBool = disctrict != null && !disctrict.isEmpty();

    if (disctrictBool) {
      sb.append(", ").append(disctrict.trim().toUpperCase());
    }

    boolean cityBool = city != null && !city.isEmpty();

    if (cityBool) {
      sb.append(" - ").append(city.trim().toUpperCase());
    }

    boolean postCodeBool = postCode != null && !postCode.isEmpty();

    if (postCodeBool) {
      sb.append(", ").append(postCode.trim());
    }

    boolean countryBool = country != null && !country.isEmpty();

    if (countryBool) {
      sb.append(", ").append(country.trim().toUpperCase());
    }

    return sb.toString();
  }

}
