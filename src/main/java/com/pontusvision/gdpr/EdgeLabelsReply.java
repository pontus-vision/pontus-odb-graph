package com.pontusvision.gdpr;

import org.janusgraph.core.EdgeLabel;
//import org.janusgraph.core.EdgeLabel;

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
public class EdgeLabelsReply {



    ReactSelectOptions[] labels;

    // must have this default constructor to get this class serialized as a reply!!!

    public EdgeLabelsReply(){


    }
    public EdgeLabelsReply(Iterable<String> edgeLabels) {

        LinkedList<ReactSelectOptions> labelsList = new LinkedList<>();

        for (String edgeLabel: edgeLabels){

            labelsList.add(new ReactSelectOptions(edgeLabel,
                    edgeLabel));

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
