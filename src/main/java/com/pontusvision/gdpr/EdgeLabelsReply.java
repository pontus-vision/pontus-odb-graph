package com.pontusvision.gdpr;

import com.orientechnologies.orient.core.metadata.schema.OClass;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class EdgeLabelsReply extends VertexLabelsReply
{

  // must have this default constructor to get this class serialized as a reply!!!

  public EdgeLabelsReply()
  {
     super();
  }

  public EdgeLabelsReply(Collection<OClass> oClasses)
  {

    super();
    LinkedList<ReactSelectOptions> labelsList = new LinkedList<>();

    for (OClass oClass : oClasses)
    {

      if (oClass.isSubClassOf(OClass.EDGE_CLASS_NAME))
      {
        labelsList.add(new ReactSelectOptions(oClass.getName().replaceAll(Pattern.quote("."), " "),
            oClass.getName()));
      }

    }
    labels = new ReactSelectOptions[labelsList.size()];
    labels = labelsList.toArray(labels);

  }

}
