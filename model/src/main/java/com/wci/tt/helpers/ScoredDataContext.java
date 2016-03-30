/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.DataContext;

/**
 * Interface representing a DataContext along with its associated probability
 * score.
 */
public interface ScoredDataContext
    extends DataContext, HasScore, Comparable<ScoredDataContext> {
  // n/a
}
