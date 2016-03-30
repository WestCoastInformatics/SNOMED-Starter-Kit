/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import com.wci.tt.TransformRecord;
import com.wci.umls.server.helpers.Configurable;

/**
 * Interface responsible for analyzing input data and context and producing
 * characterizations and statistics suitable for use cases like machine learning
 * and reporting.
 */
public interface AnalyzerHandler extends Configurable {

  /**
   * Analyze the transform record and populate the statistics and
   * characteristics. Typically analyzers and providers have to be coordinated
   * so that they use a shared set of characteristics and statistics.
   *
   * @param record the record
   * @return the transform record
   * @throws Exception the exception
   */
  public TransformRecord analyze(TransformRecord record) throws Exception;

  /**
   * Close any open resources on application shutdown.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;

}
