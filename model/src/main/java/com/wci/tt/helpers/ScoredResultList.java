/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.umls.server.helpers.ResultList;

/**
 * Represents a sortable list of {@link ScoredResult} objects.
 */
public interface ScoredResultList extends ResultList<ScoredResult> {
  // nothing extra, a simple wrapper for easy serialization
}
