/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

/**
 * Interface representing an object that has a score.
 */
public interface HasScore {

  /**
   * Sets the score.
   *
   * @param score the score
   * @throws Exception the exception
   */
  public void setScore(float score) throws Exception;

  /**
   * Returns the score.
   *
   * @return the score
   */
  public float getScore();
}
