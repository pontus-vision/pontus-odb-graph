package com.pontusvision.gdpr;

/*
{
  "annotation": {
    "name": "deploy",
    "datasource": "Simple JSON Datasource",
    "iconColor": "rgba(255, 96, 96, 1)",
    "enable": true,
    "query": "#deploy"
  }

}
 */
public class GrafanaAnnotation
{

  String name;
  String datasource;
  String iconColor;
  Boolean enable;
  String query;

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDatasource()
  {
    return datasource;
  }

  public void setDatasource(String datasource)
  {
    this.datasource = datasource;
  }

  public String getIconColor()
  {
    return iconColor;
  }

  public void setIconColor(String iconColor)
  {
    this.iconColor = iconColor;
  }

  public Boolean getEnable()
  {
    return enable;
  }

  public void setEnable(Boolean enable)
  {
    this.enable = enable;
  }

  public String getQuery()
  {
    return query;
  }

  public void setQuery(String query)
  {
    this.query = query;
  }

  public GrafanaAnnotation()
  {

  }

  public GrafanaAnnotation(String name, String datasource, String iconColor, Boolean enable, String query)
  {
    this.name = name;
    this.datasource = datasource;
    this.iconColor = iconColor;
    this.enable = enable;
    this.query = query;
  }
}
