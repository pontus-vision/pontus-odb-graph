package com.pontusvision.gdpr;

import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

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

    for (int i = 0, ilen = cols.length; i < ilen; i++)
    {
      if (i > 0)
      {
        sb.append(',');
      }
      sb.append(cols[i].id);

    }
    sb.append(' ');

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

    sb.append("( ").append(colId);

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

    sb.append("( ").append(colId);

    //    equals, greaterThan, lessThan, inRange, notEqual

//    if ("inRange".equals(type))
//    {
//      sb.append("BETWEEN  ").append(filter).append(");
//    }
    if ("notEqual".equals(type))
    {
      sb.append(" <> ").append(filter);
    }
    else if ("equals".equals(type))
    {
      sb.append(" = ").append(filter);
    }
    else if ("lessThan".equals(type))
    {
      sb.append(" < ").append(filter);
    }
    else if ("greaterThan".equals(type))
    {
      sb.append(" >  ").append(filter);
    }

    sb.append(" ) ");
    return sb;
  }

  public static StringBuilder appendWhereFiltersSQL(StringBuilder sb, PVGridFilters[] filters)
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
            {"colId":"Person.Natural.Date_Of_Birth","dateTo":null,"dateFrom":"1960-08-16","type":"equals","filterType":"date"}
            {"colId":"Person.Natural.Date_Of_Birth","dateTo":null,"dateFrom":"1932-09-02","type":"greaterThan","filterType":"date"}
            {"colId":"Person.Natural.Date_Of_Birth","dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"}
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
              "colId":"Person.Natural.Date_Of_Birth",
              "filterType":"date",
              "operator":"OR",
              "condition1":{"dateTo":"2020-06-22","dateFrom":"1960-08-16","type":"inRange","filterType":"date"},
              "condition2":{"dateTo":null,"dateFrom":"1974-08-02","type":"equals","filterType":"date"}
            }
            {
              "colId":"Person.Natural.Date_Of_Birth",
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
      sb.append(' ');
    }



    return sb;
  }



  public String getSQL(boolean isCount)
  {
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
    sb.append(" FROM ").append(dataType).append(" ");
    appendWhereFiltersSQL(sb, this.filters);
    appendCustomFilterSQL(sb, this.customFilter);

    if (!isCount)
    {
      appendOrderSQL (sb, this.sortCol, this.sortDir) ;
      appendPaginationSQL (sb, this.from, this.to);
    }

    return sb.toString();
  }

  public static StringBuilder appendCustomFilterSQL(StringBuilder sb, String customFilter)
  {
    if (StringUtils.isNotEmpty(customFilter))
    {

      String wholeStr = sb.toString();


      if ("unmatchedEvents".equalsIgnoreCase(customFilter))
      {
        sb.setLength(0);

        sb.append ("SELECT * FROM (").append(wholeStr).append(") where count(inE()) = 1");
//        resSet = resSet.where(__.inE().count().is(eq(1)));
      }
      else if ("children".equalsIgnoreCase(customFilter))
      {
        sb.setLength(0);

        long ageThresholdMs = (long) (System.currentTimeMillis() - (3600000L * 24L * 365L * 18L));
        Date dateThreshold  = new java.util.Date(ageThresholdMs);
        sb.append ("SELECT * FROM (")
          .append(wholeStr)
          .append(") where Person.Natural.Date_Of_Birth >= date('")
          .append(dateThreshold.toString())
          .append("')");
//        resSet = resSet.where(__.values("Person.Natural.Date_Of_Birth").is(gte(dateThreshold)));
      }
      else if (customFilter.startsWith("hasNeighbourId:"))
      {
//        long neighbourId = Long.parseLong(customFilter.split(":")[1]);
//        resSet = resSet.where(__.both().hasId(neighbourId));

      }

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
      sb.append(" ORDER BY ").append(sortCol).append(" ").append("+asc".equalsIgnoreCase(sortDir) ? "ASC" : "DESC");
    }
    return sb;
  }

}
