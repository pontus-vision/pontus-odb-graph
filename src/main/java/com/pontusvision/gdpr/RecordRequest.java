package com.pontusvision.gdpr;

import com.orientechnologies.orient.core.id.ORecordId;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.tinkerpop.gremlin.process.traversal.P.eq;
import static org.apache.tinkerpop.gremlin.process.traversal.P.gte;

public class RecordRequest
{
  //    {
  //        searchStr: self.searchstr,
  //                from: from,
  //            to: to,
  //            sortBy: self.sortcol,
  //            sortDir: ((self.sortdir > 0) ? "+asc" : "+desc")
  //    }

  PVGridColumn  cols[];
  PVGridFilters filters[];
  PVGridSearch  search;
  Long          from;
  Long          to;
  String        sortCol;
  String        sortDir;
  String        dataType;
  String        customFilter;

  public String getCustomFilter()
  {
    return customFilter;
  }

  public void setCustomFilter(String customFilter)
  {
    this.customFilter = customFilter;
  }

  public Long getFrom()
  {
    return from;
  }

  public void setFrom(Long from)
  {
    this.from = from;
  }

  public Long getTo()
  {
    return to;
  }

  public void setTo(Long to)
  {
    this.to = to;
  }

  public String getSortCol()
  {
    return sortCol;
  }

  public void setSortCol(String sortCol)
  {
    this.sortCol = sortCol;
  }

  public String getSortDir()
  {
    return sortDir;
  }

  public void setSortDir(String sortDir)
  {
    this.sortDir = sortDir;
  }

  public PVGridSearch getSearch()
  {
    return search;
  }

  public void setSearch(PVGridSearch search)
  {
    this.search = search;
  }

  public String getDataType()
  {
    return dataType;
  }

  public void setDataType(String dataType)
  {
    this.dataType = dataType;
  }

  public PVGridColumn[] getCols()
  {
    return cols;
  }

  public void setCols(PVGridColumn[] cols)
  {
    this.cols = cols;
  }

  public PVGridFilters[] getFilters()
  {
    return filters;
  }

  public void setFilters(PVGridFilters[] filters)
  {
    this.filters = filters;
  }

  public static StringBuilder appendColsSQL(StringBuilder sb, PVGridColumn[] cols)
  {
    int counter = 0;
    for (int i = 0, ilen = cols.length; i < ilen; i++)
    {
      if (cols[i].id.startsWith("@"))
      {
        continue;
      }

      if (counter > 0)
      {
        sb.append("`,`");
      }
      else
      {
        sb.append("@rid as id,`");
      }
      counter ++;
      sb.append(cols[i].id);

    }
    sb.append("` ");

    return sb;
  }


  public static StringBuilder addConditionFilter(StringBuilder sb, PVGridFilters filter)
  {
    if (filter.condition1 != null)
    {
      sb.append("(");
      if ("date".equalsIgnoreCase(filter.filterType))
      {
        addDateFilter(sb, filter.colId, filter.condition1.dateFrom, filter.condition1.dateTo, filter.condition1.type);
      }
      else
      {
        addTextFilter(sb, filter.colId, filter.condition1.type, filter.condition1.filter);
      }
      if (filter.condition2 != null)
      {
        sb.append(") ").append(filter.operator).append(" (");
        if ("date".equalsIgnoreCase(filter.filterType))
        {
          addDateFilter(sb, filter.colId, filter.condition2.dateFrom, filter.condition2.dateTo, filter.condition2.type);
        }
        else
        {
          addTextFilter(sb, filter.colId, filter.condition2.type, filter.condition2.filter);
        }
        //              sb.append(")");
      }
      sb.append(")");
    }
    return sb;

  }

  public static StringBuilder addFilter(StringBuilder sb, PVGridFilters filter)
  {
    if ("date".equalsIgnoreCase(filter.filterType))
    {
      return addDateFilter(sb, filter.colId, filter.dateFrom, filter.dateTo, filter.type);

    }
    else
    {
      return addTextFilter(sb, filter.colId, filter.type, filter.filter);
    }

  }

  public static StringBuilder addDateFilter(StringBuilder sb, String colId, String dateFrom, String dateTo, String type)
  {

    sb.append("( `").append(colId).append("` ");

    //    equals, greaterThan, lessThan, inRange, notEqual

    if ("inRange".equals(type))
    {
      sb.append("BETWEEN date('").append(dateFrom).append("') AND date('").append(dateTo).append("') ");
    }
    else if ("notEqual".equals(type))
    {
      sb.append(" <> date('").append(dateFrom).append("')");
    }
    else if ("equals".equals(type))
    {
      sb.append(" = date('").append(dateFrom).append("')");
    }
    else if ("lessThan".equals(type))
    {
      sb.append(" < date('").append(dateFrom).append("')");
    }
    else if ("greaterThan".equals(type))
    {
      sb.append(" > date('").append(dateFrom).append("')");
    }

    sb.append(" ) ");
    return sb;
  }


  public static StringBuilder addTextFilter(StringBuilder sb, String colId, String type, String filter)
  {

    if (type.startsWith("not"))
    {
      sb.append("( NOT `").append(colId).append("` ");
    }
    else
    {
      sb.append("( `").append(colId).append("` ");

    }


    //    equals, greaterThan, lessThan, inRange, notEqual

//    if ("inRange".equals(type))
//    {
//      sb.append("BETWEEN  ").append(filter).append(");
//    }
    if ("notEqual".equals(type))
    {
      sb.append(" <> '").append(filter).append("'");;
    }
    else if ("equals".equals(type))
    {
      sb.append(" = '").append(filter).append("'");;
    }
    else if ("lessThan".equals(type))
    {
      sb.append(" < '").append(filter).append("'");;
    }
    else if ("greaterThan".equals(type))
    {
      sb.append(" >  '").append(filter).append("'");;
    }
    else if ("contains".equals(type) ||"notContains".equals(type) )
    {
      sb.append(" containsText  '").append(filter).append("'");
    }
    else if ("startsWith".equals(type))
    {
      sb.append(" LIKE  '").append(filter).append("%'");

    }
    else if ("endsWith".equals(type))
    {
      sb.append(" LIKE  '%").append(filter).append("'");

    }
    sb.append(" ) ");
    return sb;
  }

  public static StringBuilder appendWhereFiltersSQL(StringBuilder sb, PVGridFilters[] filters, String customFilter)
  {

    if (filters != null)
    {
      for (int i = 0, ilen = filters.length; i < ilen; i++)
      {
        PVGridFilters filter = filters[i];
        if (i == 0)
        {
          sb.append(" WHERE (");
        }
        else
        {
          sb.append(") AND (");
        }
         /* when we have simple filters, the following format is used:
          [
            { colId: "Object_Notification_Templates_Label", filterType: "text", type: "contains", filter: "adfasdf"},
            { colId: "Object_Notification_Templates_Types", filterType: "text", type: "contains", filter: "aaa"}
            {"colId":"Person_Natural_Date_Of_Birth","dateTo":null,"dateFrom":"1960-08-16","type":"equals","filterType":"date"}
            {"colId":"Person_Natural_Date_Of_Birth","dateTo":null,"dateFrom":"1932-09-02","type":"greaterThan","filterType":"date"}
            {"colId":"Person_Natural_Date_Of_Birth","dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"}
          ]
         */

        if (filter.operator == null)
        {
          addFilter(sb, filter);
        }
        /*
          When we have complex filters, the following format is used:
          [
            {
              colId: "Object_Notification_Templates_Label",
              condition1: {filterType: "text", type: "notContains", filter: "ddd"},
              condition2: {filterType: "text", type: "endsWith", filter: "aaaa"},
              filterType: "text",
              operator: "OR"
            },
            {
              colId: "Object_Notification_Templates_Types:{
              condition1: {filterType: "text", type: "notContains", filter: "aaaa"},
              condition2: {filterType: "text", type: "startsWith", filter: "bbbb"},
              filterType: "text",
              operator: "AND"
            }
            {
              "colId":"Person_Natural_Date_Of_Birth",
              "filterType":"date",
              "operator":"OR",
              "condition1":{"dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"},
              "condition2":{"dateTo":null,"dateFrom":"1974-08-02","type":"equals","filterType":"date"}
            }
            {
              "colId":"Person_Natural_Date_Of_Birth",
              "filterType":"date",
              "operator":"OR",
              "condition1":{"dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"},
              "condition2":{"dateTo":null,"dateFrom":"1974-08-02","type":"notEqual","filterType":"date"}
            }
          ]
         */
        else
        {
          addConditionFilter(sb, filter);
        }

      }
      if (filters.length > 0)
      {
        sb.append(')');
      }
      appendCustomFilterSQL(sb, customFilter,filters.length > 0);

      sb.append(' ');
    }
    else
    {
      appendCustomFilterSQL(sb, customFilter,false);

    }



    return sb;
  }

  public String getDirStatement(){
    StringBuilder sb = new StringBuilder();
    sb.append((this.search.direction.equals("<-") ? "IN(" : "OUT("));
    sb.append("'").append(this.search.relationship).append("'").append(')');
    return sb.toString();
  }

  public String getSQLNeighbour(boolean isCount){
    StringBuilder sb = new StringBuilder();

    String dir = this.getDirStatement();

    if (isCount){
      sb.append("SELECT COUNT(*) FROM (SELECT EXPAND(").append(dir).append(") FROM ")
              .append(this.search.vid).append(")");
    }
    else{
      sb.append("SELECT ");
      appendColsSQL(sb, this.cols);
      sb.append(" FROM (");

      sb.append("SELECT EXPAND(").append(dir).append(") FROM ").append(this.search.vid).append(" ");
      appendWhereFiltersSQL(sb, this.filters, this.customFilter);
      sb.append(")");
    }

    return sb.toString();

  }
  public String getSQLNormal(boolean isCount){
    StringBuilder sb = new StringBuilder();

    if (isCount)
    {
      sb.append("SELECT COUNT(*) ");
    }
    else
    {
      sb.append("SELECT ");
      appendColsSQL(sb, this.cols);
    }
    sb.append(" FROM `").append(dataType).append("` ");
    appendWhereFiltersSQL(sb, this.filters, this.customFilter);

    return sb.toString();

  }


  public String getSQL(boolean isCount)
  {
    StringBuilder sb = new StringBuilder();

    if (this.search.vid != null){
      sb.append(getSQLNeighbour(isCount));
    }
    else {
      sb.append(getSQLNormal(isCount));
    }

    if (!isCount)
    {
      appendOrderSQL (sb, this.sortCol, this.sortDir) ;
      appendPaginationSQL (sb, this.from, this.to);
    }

    return sb.toString();
  }

  public static StringBuilder appendCustomFilterSQL(StringBuilder sb, String customFilter, boolean hasOtherFilters)
  {
    if (StringUtils.isNotEmpty(customFilter))
    {

      if (hasOtherFilters)
      {
        sb.append(" AND (");
      }
      else
      {
        sb.append(" WHERE (");
      }


      if ("unmatchedEvents".equalsIgnoreCase(customFilter))
      {

        sb.append (" inE().size() = 1 ");
//        resSet = resSet.where(__.inE().count().is(eq(1)));
      }
      else if ("children".equalsIgnoreCase(customFilter))
      {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        long ageThresholdMs = (long) (System.currentTimeMillis() - (3600000L * 24L * 365L * 18L));
        Date dateThreshold  = new java.util.Date(ageThresholdMs);
        sb.append (
            " `Person_Natural_Date_Of_Birth` >= date('")
          .append((dateFormat).format(dateThreshold))
          .append("')");
//        resSet = resSet.where(__.values("Person_Natural_Date_Of_Birth").is(gte(dateThreshold)));
      }
      else if (customFilter.startsWith("hasNeighbourId:"))
      {
        String neighbourId = customFilter.substring("hasNeighbourId:".length());
        ORecordId recordId = new ORecordId(neighbourId); // try to instantiate to validate, and prevent sql injection

        sb.append(recordId.toString()).append(" IN both() ");

//        long neighbourId = Long.parseLong(customFilter.split(":")[1]);
//        resSet = resSet.where(__.both().hasId(neighbourId));


      }
      sb.append(")");

    }

    return sb;
  }

  public static StringBuilder appendPaginationSQL(StringBuilder sb, Long from, Long to)
  {
    if (from != null && to != null)
    {
      sb.append(" SKIP ").append(from).append(" LIMIT ").append(to - from);
    }

    return sb;
  }

  public static StringBuilder appendOrderSQL(StringBuilder sb, String sortCol, String sortDir)
  {
    if (sortCol != null)
    {
      sb.append(" ORDER BY `").append(sortCol).append("` ").append("+asc".equalsIgnoreCase(sortDir) ? "ASC" : "DESC");
    }
    return sb;
  }

}
