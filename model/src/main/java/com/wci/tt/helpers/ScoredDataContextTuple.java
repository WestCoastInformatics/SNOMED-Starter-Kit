/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

/**
 * Interface representing data along with its associated DataContext and
 * probability.
 */
public interface ScoredDataContextTuple
    extends DataContextTuple, HasScore, Comparable<ScoredDataContextTuple> {
  // n/a
}
