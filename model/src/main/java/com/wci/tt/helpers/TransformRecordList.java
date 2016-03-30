/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.TransformRecord;
import com.wci.umls.server.helpers.ResultList;

/**
 * Represents multiple data contexts via using list of {@link TransformRecord}.
 * 
 * Useful for sending input and output data contexts to REST Server. In such
 * case, Input is always field always field #0 and output always field #1.
 */
public interface TransformRecordList extends ResultList<TransformRecord> {
  // nothing extra, a simple wrapper for easy serialization
}
