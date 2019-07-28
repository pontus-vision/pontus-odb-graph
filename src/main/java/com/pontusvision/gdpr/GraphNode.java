package com.pontusvision.gdpr;

import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.regex.Pattern;

public class GraphNode
{
  static final String METADATA = "Metadata.";
  static final Escaper percentEscaper = new PercentEscaper("-_.*", false);

  Long id;
  String label = "";
  String shape;
  String title;
  String image = "";

  public GraphNode()
  {
    this.shape = "image";

  }

  public GraphNode(Vertex v)
  {
    this();
    this.id = (Long) v.id();
    String vLabel = v.label();

    this.label = ""; //vLabel;

    int vLabelSize = vLabel.length() + 1; // +1 for the dot.

    //    this.label = vLabel;
    //    "data:image/svg+xml;charset=utf-8,"+

    //    StringBuilder sb = new StringBuilder(vLabel).append("\n");
    StringBuilder sb = new StringBuilder();

    this.title = "<h1> title </h1>";
    Iterator<VertexProperty<Object>> properties = v.properties();
    while (properties.hasNext())
    {
      VertexProperty<Object> prop = properties.next();
      String label = prop.label();

      sb.append("<tr><td class=\"tg-yw4l\">");

      if (label.startsWith(vLabel))
      {
        sb.append(label.substring(vLabelSize));

      }
      else if (label.startsWith(METADATA))
      {
        sb.append(label.substring(METADATA.length()));

      }
      else
      {
        sb.append(label);
      }
      sb.append("</td><td class=\"tg-yw4l\">");

      sb.append(prop.value().toString()).append("</td></tr>");

    }

    StringBuilder svgHeadSb = new StringBuilder(
        "<svg xmlns=\"http://www.w3.org/2000/svg\" preserveAspectRatio=\"xMidYMid meet\" height=\"1200\" width=\"1000\" viewPort=\"0 0 1200 1000\" >")
        .append("<foreignObject height=\"100%\" width=\"100%\"  fill=\"#797979\" stroke-width=\"20\" stroke=\"#ffffff\"  >");

    StringBuilder tableBodySb = new StringBuilder()
        .append("<div xmlns=\"http://www.w3.org/1999/xhtml\" style=\"font-size:40px; font-color: #FFFFFF;   height:100%; width:100%; \">")
        .append("<style>")
        .append(
            ".tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:visible;word-break:normal;}")
        .append(
            ".tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:visible;word-break:normal;}")
        .append(".tg .tg-ygl1{font-weight:bold;background-color:#9b9b9b}")
        .append(".tg .tg-x9s4{font-weight:bold;background-color:#9b9b9b;vertical-align:top}")
        .append(".tg .tg-yw4l{vertical-align:top}")
        .append("</style>")
        .append("<div style=\"flex-direction: column; justify-content: center; align-content:center;\">")

        .append("<h3 style=\"color: white; font-color: white;\">").append(vLabel.replaceAll("[_.]"," ")).append("</h3>")

        .append("<table class=\"tg\" style=\" overflow: visible; background: #595959; height: calc(100%); width: 600px\">")
        .append("<colgroup> <col style=\"width: 30%\"/><col style=\"width: 70%\"/></colgroup>")
        .append("<tr><th class=\"tg-ygl1\">Property</th><th class=\"tg-x9s4\">Value</th></tr>")
        .append(sb)
        .append("</table>").append("</div></div>");

    StringBuilder svgFootSb = new StringBuilder("</foreignObject></svg>");

    StringBuilder imageSb = new StringBuilder("data:image/svg+xml;charset=utf-8,");

    StringBuilder svgSb = new StringBuilder().append(svgHeadSb).append(tableBodySb).append(svgFootSb);

    imageSb.append(percentEscaper.escape(svgSb.toString()));

    this.image = imageSb.toString().replaceAll(Pattern.quote("nbsp"),
        "#160"); //percentEscaper.escape(tableBodySb.toString()).replaceAll("&nbsp;","&#160;");
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getShape()
  {
    return shape;
  }

  public void setShape(String shape)
  {
    this.shape = shape;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public String getImage()
  {
    return image;
  }

  public void setImage(String image)
  {
    this.image = image;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (!(o instanceof GraphNode))
      return false;

    GraphNode graphNode = (GraphNode) o;

    return id.equals(graphNode.id);
  }

  @Override public int hashCode()
  {
    return id.hashCode();
  }

}
