package com.pontusvision.gdpr;

import java.util.List;

public class DiscoveryRequest{
  public List<ColMetaData> colMetaData;
  public String tableCatalog;
  public String tableName;
  public String fqn;
  public String tableType;
  public String tableRemarks;
  public Double percentThreshold = 0.8;
  public String regexPattern;
}

