package com.pontusvision.gdpr;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.HashSet;
import java.util.Set;

public class GraphReply extends BaseReply
{
  GraphNode nodes[];

  GraphEdge edges[];

  public GraphReply()
  {
    super();
    nodes = null;
    edges = null;
  }

  public GraphReply(Status status, String err)
  {
    super(status,err);

  }
  public GraphReply(Vertex vtx, Set<Vertex> inNodes, Set<Vertex> outNodes, Set<Edge> inEdges, Set<Edge> outEdges)
  {
    super();
    Set<GraphNode> nodes = new HashSet<>();

    GraphNode vtxNode = new GraphNode(vtx);
    nodes.add(vtxNode);


    for (Vertex v : inNodes)
    {
      GraphNode node = new GraphNode(v);
      nodes.add(node);
    }
    for (Vertex v : outNodes)
    {
      GraphNode node = new GraphNode(v);
      nodes.add(node);
    }

    Set<GraphEdge> edges = new HashSet<>();
    for (Edge edge : inEdges)
    {
      GraphEdge e = new GraphEdge(edge, true);
      edges.add(e);
      nodes.add(new GraphNode(edge.inVertex()));
      nodes.add(new GraphNode(edge.outVertex()));

    }
    for (Edge edge : outEdges)
    {
      GraphEdge e = new GraphEdge(edge, false);
      edges.add(e);
      nodes.add(new GraphNode(edge.inVertex()));
      nodes.add(new GraphNode(edge.outVertex()));

    }

    this.nodes = nodes.toArray(new GraphNode[nodes.size()]);
    this.edges = edges.toArray(new GraphEdge[edges.size()]);

  }

  public GraphNode[] getNodes()
  {
    return nodes;
  }

  public void setNodes(GraphNode[] nodes)
  {
    this.nodes = nodes;
  }

  public GraphEdge[] getEdges()
  {
    return edges;
  }

  public void setEdges(GraphEdge[] edges)
  {
    this.edges = edges;
  }

  //
  //  nodes: [
  //  {id: 1, label: 'Jackson Turner', color: '#e04141'},
  //  {id: 2, label: 'Megan Perry', color: '#e09c41'},
  //  {id: 3, label: 'Ryan Harris', color: '#e0df41'},
  //  {id: 4, label: 'Jennifer Edwards', color: '#7be041'},
  //  {id: 5, label: 'Noah Jenkins', color: '#41e0c9'}
  //        ],
  //  edges: [
  //  {from: 1, to: 2},
  //  {from: 1, to: 3},
  //  {from: 2, to: 4},
  //  {from: 2, to: 5}
  //        ]

}
