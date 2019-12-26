package com.pontusvision.gdpr;

import com.orientechnologies.orient.core.metadata.schema.OClass;

import java.util.Collection;
import java.util.LinkedList;

//import org.janusgraph.core.EdgeLabel;

/*
{
      from: from
     ,to: to
     ,totalAvailable: records.length
     ,records: records.slice(from, to)

    }
 */
public class EdgeLabelsReply
{

  ReactSelectOptions[] labels;

  // must have this default constructor to get this class serialized as a reply!!!

  public EdgeLabelsReply()
  {

  }

  public EdgeLabelsReply(Collection<OClass> edgeLabels)
  {

    LinkedList<ReactSelectOptions> labelsList = new LinkedList<>();

    for (OClass edgeLabel : edgeLabels)
    {

      labelsList.add(new ReactSelectOptions(edgeLabel.getName(),
          edgeLabel.getName()));

    }
    labels = new ReactSelectOptions[labelsList.size()];
    labels = labelsList.toArray(labels);

  }

  public ReactSelectOptions[] getLabels()
  {
    return labels;
  }

  public void setLabels(ReactSelectOptions[] labels)
  {
    this.labels = labels;
  }
}
