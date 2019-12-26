package com.pontusvision.gdpr;

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
public class NodePropertyNamesReply
{

  ReactSelectOptions[] labels;

  // must have this default constructor to get this class serialized as a reply!!!

  public NodePropertyNamesReply()
  {

  }

  public NodePropertyNamesReply(Iterable<String> vertexProperties)
  {

    LinkedList<ReactSelectOptions> labelsList = new LinkedList<>();

    for (String vertexLabel : vertexProperties)
    {
      if (vertexLabel.startsWith("@"))
      {
          String cleanLabel = vertexLabel.replaceFirst("^@", "");
          cleanLabel = cleanLabel.replaceAll("@.*", "");
          labelsList.add(new ReactSelectOptions(cleanLabel,
              vertexLabel));

      }
      else
      {
        String cleanLabel = vertexLabel.replaceFirst("^#", "");
        cleanLabel = cleanLabel.replaceAll(Pattern.quote("."), " ");

        labelsList.add(new ReactSelectOptions(cleanLabel,
            vertexLabel));
      }
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
