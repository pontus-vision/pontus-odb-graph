package com.pontusvision.gdpr;

import org.apache.tinkerpop.gremlin.structure.Edge;

public class GraphEdge
{

  Long from;
  Long to;

  String label;

  public GraphEdge()
  {

  }

  public GraphEdge(Edge edge, boolean fromToNormalOrder)
  {
    if (fromToNormalOrder)
    {
      from = Long.parseLong(edge.inVertex().id().toString());
      to = Long.parseLong(edge.outVertex().id().toString());
    }
    else
    {
      from = Long.parseLong(edge.outVertex().id().toString());
      to = Long.parseLong(edge.inVertex().id().toString());
    }
    this.label = edge.label();
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (!(o instanceof GraphEdge))
      return false;

    GraphEdge graphEdge = (GraphEdge) o;

    if (!from.equals(graphEdge.from))
      return false;
    if (!to.equals(graphEdge.to))
      return false;
    return label != null ? label.equals(graphEdge.label) : graphEdge.label == null;
  }

  @Override public int hashCode()
  {
    int result = from.hashCode();
    result = 31 * result + to.hashCode();
    result = 31 * result + (label != null ? label.hashCode() : 0);
    return result;
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

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }
}
