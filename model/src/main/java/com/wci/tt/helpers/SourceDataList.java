/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.SourceData;
import com.wci.umls.server.helpers.ResultList;
import com.wci.umls.server.helpers.SearchResult;

/**
 * Represents a sortable list of {@link SearchResult} objects.
 */
public interface SourceDataList extends ResultList<SourceData> {
  // nothing extra, a simple wrapper for easy serialization
}
