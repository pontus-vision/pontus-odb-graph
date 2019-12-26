package com.pontusvision.gdpr;

import com.orientechnologies.orient.core.metadata.schema.OClass;

import java.util.Collection;
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
public class VertexLabelsReply
{

  ReactSelectOptions[] labels;

  // must have this default constructor to get this class serialized as a reply!!!

  public VertexLabelsReply()
  {

  }

  public VertexLabelsReply(Collection<OClass> oClasses)
  {

    LinkedList<ReactSelectOptions> labelsList = new LinkedList<>();

    for (OClass oClass : oClasses)
    {

      if (oClass.isSubClassOf(OClass.VERTEX_CLASS_NAME))
      {
        labelsList.add(new ReactSelectOptions(oClass.getName().replaceAll(Pattern.quote("."), " "),
            oClass.getName()));
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
