/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.Properties;

import com.wci.tt.TransformRecord;
import com.wci.tt.services.handlers.AnalyzerHandler;

/**
 * Default implementation of {@link AnalyzerHandler} that computes the length of
 * the input string.
 */
public class DefaultAnalyzer implements AnalyzerHandler {

  /** The input length. */
  public final String INPUT_LENGTH = "INPUT_LENGTH";

  /**
   * Instantiates an empty {@link DefaultAnalyzer}.
   */
  public DefaultAnalyzer() {
    // n/a
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Default Analyzer";
  }

  /* see superclass */
  @Override
  public void setProperties(Properties arg0) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public TransformRecord analyze(TransformRecord record) throws Exception {

    record.getStatistics().put(INPUT_LENGTH, record.getInputString() != null
        ? record.getInputString().length() : 0d);

    return record;
  }

  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }

}
