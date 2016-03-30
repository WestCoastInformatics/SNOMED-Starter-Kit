/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.umls.server.helpers.ResultList;

/**
 * Represents a sortable list of {@link ScoredDataContext}.
 */
public interface ScoredDataContextList extends ResultList<ScoredDataContext> {
  // nothing extra, a simple wrapper for easy serialization
}
