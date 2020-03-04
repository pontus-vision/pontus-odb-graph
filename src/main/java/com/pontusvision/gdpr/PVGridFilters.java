package com.pontusvision.gdpr;

public class PVGridFilters
{
    String colId;

    String filterType;

    String type;

    String filter;



    String dateTo;
    String dateFrom;

    PVGridFilterCondition condition1;
    PVGridFilterCondition condition2;

    String operator;


    public PVGridFilterCondition getCondition1()
    {
        return condition1;
    }

    public void setCondition1(PVGridFilterCondition condition1)
    {
        this.condition1 = condition1;
    }

    public PVGridFilterCondition getCondition2()
    {
        return condition2;
    }

    public void setCondition2(PVGridFilterCondition condition2)
    {
        this.condition2 = condition2;
    }
    public String getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(String dateTo)
    {
        this.dateTo = dateTo;
    }

    public String getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom)
    {
        this.dateFrom = dateFrom;
    }
    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }



    public String getColId()
    {
        return colId;
    }

    public void setColId(String colId)
    {
        this.colId = colId;
    }

    public String getFilterType()
    {
        return filterType;
    }

    public void setFilterType(String filterType)
    {
        this.filterType = filterType;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public PVGridFilters()
    {
    }
}
