package com.pontusvision.gdpr;

import java.util.List;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */
public class ColMatchProbability
{
  public String poleType;
  public double    probability;

  @Override public String toString()
  {
    return "{" +
        "poleType:\"" + poleType + "\"" +
        ", probability:" + probability +
        '}';
  }

  public ColMatchProbability(String poleType, double probability)
  {
    this.poleType = poleType;
    this.probability = probability;
  }
}
