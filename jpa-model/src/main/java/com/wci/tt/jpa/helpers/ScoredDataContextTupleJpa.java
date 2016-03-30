/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.ScoredDataContextTuple;

/**
 * JPA enabled implementation of {@link ScoredDataContextTuple}.
 */
@XmlRootElement(name = "scoredDataContextTuple")
public class ScoredDataContextTupleJpa extends DataContextTupleJpa
    implements ScoredDataContextTuple, Comparable<ScoredDataContextTuple> {

  /** The context associated with the data. */
  private float score = 0;

  /**
   * Instantiates an empty {@link ScoredDataContextTupleJpa}.
   */
  public ScoredDataContextTupleJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ScoredDataContextTupleJpa} from the specified
   * parameters.
   *
   * @param tuple the data context tuple
   */
  public ScoredDataContextTupleJpa(ScoredDataContextTuple tuple) {
    super(tuple);
    this.score = tuple.getScore();
  }

  /* see superclass */
  @Override
  public float getScore() {
    return score;
  }

  /* see superclass */
  @Override
  public void setScore(float score) throws Exception {
    if (score < 0 || score > 1) {
      throw new Exception("Score must be between 0-1 inclusive");
    }

    this.score = score;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Float.floatToIntBits(score);
    return result;
  }

  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ScoredDataContextTupleJpa other = (ScoredDataContextTupleJpa) obj;
    if (score != other.score)
      return false;
    if (!super.equals(other))
      return false;

    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredDataContextTupleJpa [" + super.toString() + "]" + ", score="
        + score + "]";
  }

  /* see superclass */
  @Override
  public int compareTo(ScoredDataContextTuple o) {
    Float score1 = new Float(this.getScore());
    Float score2 = new Float(o.getScore());

    return score2.compareTo(score1);
  }
}
