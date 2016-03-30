/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.umls.server.helpers.ResultList;

/**
 * Represents a sortable list of {@link ScoredDataContextTuple}.
 */
public interface ScoredDataContextTupleList
    extends ResultList<ScoredDataContextTuple> {
  // nothing extra, a simple wrapper for easy serialization
}
