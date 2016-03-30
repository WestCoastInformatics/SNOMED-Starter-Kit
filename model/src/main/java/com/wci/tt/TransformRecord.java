/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt;

import java.util.List;
import java.util.Map;

import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.HasId;
import com.wci.umls.server.helpers.HasLastModified;

/**
 * Generically represents a tuple used for processing. It references the input,
 * normalized, and output data strings as well as the identified input context
 * and required output context.
 */
public interface TransformRecord extends HasId, HasLastModified {

  /**
   * Returns the input string.
   *
   * @return the input string
   */
  public String getInputString();

  /**
   * Sets the input string.
   *
   * @param inputString the input string
   */
  public void setInputString(String inputString);

  /**
   * Returns the normalized input strings.
   *
   * @return the normalized input strings
   */
  public List<ScoredResult> getNormalizedResults();

  /**
   * Sets the normalized input strings.
   *
   * @param normalizedResults the normalized results
   */
  public void setNormalizedResults(List<ScoredResult> normalizedResults);

  /**
   * Returns the input context.
   *
   * @return the input context
   */
  public DataContext getInputContext();

  /**
   * Sets the input context.
   *
   * @param inputContext the input context
   */
  public void setInputContext(DataContext inputContext);

  /**
   * Returns the output strings.
   *
   * @return the output string
   */
  public List<ScoredResult> getOutputs();

  /**
   * Sets the output strings.
   *
   * @param outputs the outputs
   */
  public void setOutputs(List<ScoredResult> outputs);

  /**
   * Returns the output context.
   *
   * @return the output context
   */
  public DataContext getOutputContext();

  /**
   * Sets the output context.
   *
   * @param outputContext the output context
   */
  public void setOutputContext(DataContext outputContext);

  /**
   * Returns the provider output context.
   *
   * @return the provider output context
   */
  public DataContext getProviderOutputContext();

  /**
   * Sets the provider output context.
   *
   * @param outputContext the output context
   */
  public void setProviderOutputContext(DataContext outputContext);

  /**
   * Returns the characteristics.
   *
   * @return the characteristics
   */
  public Map<String, String> getCharacteristics();

  /**
   * Sets the characteristics.
   *
   * @param characteristics the characteristics
   */
  public void setCharacteristics(Map<String, String> characteristics);

  /**
   * Returns the statistics.
   *
   * @return the statistics
   */
  public Map<String, Double> getStatistics();

  /**
   * Sets the statistics.
   *
   * @param statistics the statistics
   */
  public void setStatistics(Map<String, Double> statistics);

  /**
   * Returns all terms to be processed. Likely by providers.
   *
   * @return the terms to process
   * @throws Exception the exception
   */
  public List<ScoredResult> getValuesToProcess() throws Exception;

}
