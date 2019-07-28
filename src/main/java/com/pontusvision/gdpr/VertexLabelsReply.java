package com.pontusvision.gdpr;

import org.janusgraph.core.VertexLabel;

import java.util.LinkedList;
import java.util.regex.Pattern;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal.Symbols.to;

/*
{
      from: from
     ,to: to
     ,totalAvailable: records.length
     ,records: records.slice(from, to)

    }
 */
public class VertexLabelsReply {



    ReactSelectOptions[] labels;

    // must have this default constructor to get this class serialized as a reply!!!

    public VertexLabelsReply(){


    }
    public VertexLabelsReply(Iterable<VertexLabel> vertexLabels) {

        LinkedList<ReactSelectOptions> labelsList = new LinkedList<>();

        for (VertexLabel vertexLabel: vertexLabels){

            labelsList.add(new ReactSelectOptions(vertexLabel.name().replaceAll(Pattern.quote(".")," "),
                    vertexLabel.name()));

        }
        labels = new ReactSelectOptions[labelsList.size()];
        labels = labelsList.toArray(labels);

    }

    public ReactSelectOptions [] getLabels() {
        return labels;
    }

    public void setLabels(ReactSelectOptions[] labels) {
        this.labels = labels;
    }
}
