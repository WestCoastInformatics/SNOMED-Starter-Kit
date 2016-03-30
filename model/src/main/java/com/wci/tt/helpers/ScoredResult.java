/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.umls.server.helpers.HasId;

/**
 * Interface representing a search result along with its associated probability
 * score.
 */
public interface ScoredResult
    extends HasScore, HasId, Comparable<ScoredResult> {

  /**
   * Returns the value.
   *
   * @return the value
   */
  public String getValue();

  /**
   * Sets the value.
   *
   * @param value the value to set
   */
  public void setValue(String value);

  /**
   * Indicates whether or not obsolete is the case.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isObsolete();

  /**
   * Sets the obsolete.
   *
   * @param obsolete the obsolete
   */
  public void setObsolete(boolean obsolete);

}
