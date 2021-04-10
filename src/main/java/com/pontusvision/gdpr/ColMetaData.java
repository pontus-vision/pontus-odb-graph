package com.pontusvision.gdpr;

import java.util.List;
import java.util.Objects;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */
public class ColMetaData
{
  public String       colName;
  public String       primaryKeyName;
  public String       foreignKeyName;
  public String       typeName;
  public String       colRemarks;
  public String       isAutoIncr;
  public String       isGenerated;
  public int          octetLen;
  public int          ordinalPos;
  public String       defVal;
  public int          colSize;
  public List<String> vals;

  @Override public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (!(o instanceof ColMetaData))
      return false;
    ColMetaData that = (ColMetaData) o;
    return octetLen == that.octetLen &&
        ordinalPos == that.ordinalPos &&
        colSize == that.colSize &&
        colName.equals(that.colName) &&
        Objects.equals(primaryKeyName, that.primaryKeyName) &&
        Objects.equals(foreignKeyName, that.foreignKeyName) &&
        Objects.equals(typeName, that.typeName) &&
        Objects.equals(colRemarks, that.colRemarks) &&
        Objects.equals(isAutoIncr, that.isAutoIncr) &&
        Objects.equals(isGenerated, that.isGenerated) &&
        Objects.equals(defVal, that.defVal);
  }

  @Override public int hashCode()
  {
    return Objects
        .hash(colName, primaryKeyName, foreignKeyName, typeName, colRemarks, isAutoIncr, isGenerated, octetLen,
            ordinalPos, defVal, colSize);
  }

  @Override public String toString()
  {
    return colName ;
  }
}
