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
public class FormioSelectResults extends BaseReply
{

  ReactSelectOptions[] labels;

  // must have this default constructor to get this class serialized as a reply!!!

  public FormioSelectResults()
  {
    super();

  }

  public FormioSelectResults(Status status, String err)
  {
    super(status,err);
  }
  public FormioSelectResults(List<ReactSelectOptions> selectOptions)
  {
    super();
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
