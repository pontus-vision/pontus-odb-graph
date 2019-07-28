package com.pontusvision.gdpr;

import java.util.List;

/*
{
      from: from
     ,to: to
     ,totalAvailable: records.length
     ,records: records.slice(from, to)

    }
 */
public class FormioSelectResults
{

  ReactSelectOptions[] labels;

  // must have this default constructor to get this class serialized as a reply!!!

  public FormioSelectResults()
  {

  }

  public FormioSelectResults(List<ReactSelectOptions> selectOptions)
  {
    this.labels = selectOptions.toArray(new ReactSelectOptions[0]);
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
