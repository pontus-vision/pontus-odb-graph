package com.pontusvision.gdpr;

public class PVGridSearch
{
  PVGridColumn cols[];
  ReactSelectOptions extraSearch;
  String relationship;
  String direction;
  String vid;
  String searchStr;
  Boolean searchExact;


  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String relationship) {
    this.relationship = relationship;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getVid() {
    return vid;
  }

  public void setVid(String vid) {
    this.vid = vid;
  }
  public Boolean getSearchExact()
  {
    return searchExact;
  }

  public void setSearchExact(Boolean searchExact)
  {
    this.searchExact = searchExact;
  }

  public ReactSelectOptions getExtraSearch()
  {
    return extraSearch;
  }

  public void setExtraSearch(ReactSelectOptions extraSearch)
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

  public PVGridSearch()
  {
  }


}
