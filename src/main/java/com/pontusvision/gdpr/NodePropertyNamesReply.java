package com.pontusvision.gdpr;

import org.janusgraph.core.VertexLabel;

import java.util.LinkedList;
import java.util.regex.Pattern;

/*
{
      from: from
     ,to: to
     ,totalAvailable: records.length
     ,records: records.slice(from, to)

    }
 */
public class NodePropertyNamesReply {



    ReactSelectOptions[] labels;

    // must have this default constructor to get this class serialized as a reply!!!

    public NodePropertyNamesReply(){


    }
    public NodePropertyNamesReply(Iterable<String> vertexProperties) {

        LinkedList<ReactSelectOptions> labelsList = new LinkedList<>();

        for (String vertexLabel: vertexProperties){

            labelsList.add(new ReactSelectOptions(vertexLabel.replaceAll(Pattern.quote(".")," "),
                    vertexLabel));

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
