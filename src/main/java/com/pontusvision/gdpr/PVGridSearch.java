package com.pontusvision.gdpr;

public class PVGridSearch
{
  PVGridColumn cols[];

  ReactSelectOptions extraSearch[];

  String searchStr;
  Boolean searchExact;

  public Boolean getSearchExact()
  {
    return searchExact;
  }

  public void setSearchExact(Boolean searchExact)
  {
    this.searchExact = searchExact;
  }

  public ReactSelectOptions[] getExtraSearch()
  {
    return extraSearch;
  }

  public void setExtraSearch(ReactSelectOptions[] extraSearch)
  {
    this.extraSearch = extraSearch;
  }

  public String getSearchStr()
  {
    return searchStr;
  }

  public void setSearchStr(String searchStr)
  {
    this.searchStr = searchStr;
  }

  public PVGridColumn[] getCols()
  {
    return cols;
  }

  public void setCols(PVGridColumn[] cols)
  {
    this.cols = cols;
  }
}
