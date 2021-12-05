package com.pontusvision.gdpr;

/*
{
      from: from
     ,to: to
     ,totalAvailable: records.length
     ,records: records.slice(from, to)

    }
 */
public class RecordReply extends BaseReply
{
  Long from;
  Long to;
  Long totalAvailable;
  String[] records;

  public Long getFrom()
  {
    return from;
  }

  public void setFrom(Long from)
  {
    this.from = from;
  }

  public Long getTo()
  {
    return to;
  }

  public void setTo(Long to)
  {
    this.to = to;
  }

  public Long getTotalAvailable()
  {
    return totalAvailable;
  }

  public void setTotalAvailable(Long totalAvailable)
  {
    this.totalAvailable = totalAvailable;
  }

  public String[] getRecords()
  {
    return records;
  }

  public void setRecords(String[] records)
  {
    this.records = records;
  }

  // must have this default constructor to get this class serialized as a reply!!!
  public RecordReply()
  {
    super();

  }
  public RecordReply(Status status, String err){
    super(status,err);
  }

  public RecordReply(Long from, Long to, Long totalAvailable, String[] records)
  {
    super();
    this.from = from;
    this.to = to;
    this.totalAvailable = totalAvailable;
    this.records = records;
  }

}
