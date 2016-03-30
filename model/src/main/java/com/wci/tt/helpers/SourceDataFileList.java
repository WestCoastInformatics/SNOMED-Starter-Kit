/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.SourceDataFile;
import com.wci.umls.server.helpers.ResultList;

/**
 * Represents a sortable list of {@link SourceDataFile} objects.
 */
public interface SourceDataFileList extends ResultList<SourceDataFile> {
  // nothing extra, a simple wrapper for easy serialization
}
