/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.jpa.DataContextJpa;

/**
 * JPA enabled implementation of {@link ScoredDataContext}.
 */
@XmlRootElement(name = "scoredDataContext")
public class ScoredDataContextJpa extends DataContextJpa
    implements ScoredDataContext, Comparable<ScoredDataContext> {

  /** The score. */
  private float score = 0;

  /**
   * Instantiates an empty {@link DataContextJpa} with an associated score.
   */
  public ScoredDataContextJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ScoredDataContextJpa} from the specified parameters.
   *
   * @param result the scored result
   */
  public ScoredDataContextJpa(ScoredDataContext result) {
    super(result);
    this.score = result.getScore();
  }

  /**
   * Instantiates a {@link ScoredDataContextJpa} from the specified parameters.
   *
   * @param result the result
   */
  public ScoredDataContextJpa(DataContext result) {
    super(result);
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
    ScoredDataContextJpa other = (ScoredDataContextJpa) obj;
    if (score != other.score)
      return false;
    if (!super.equals(other))
      return false;

    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredDataContextJpa [" + super.toString() + "]" + ", score="
        + score + "]";
  }

  /* see superclass */
  @Override
  public int compareTo(ScoredDataContext o) {
    Float score1 = new Float(this.getScore());
    Float score2 = new Float(o.getScore());

    return score2.compareTo(score1);
  }
}
