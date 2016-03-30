/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;
import java.util.Map;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.Configurable;

/**
 * Interface responsible for functionality associated with a given
 * representation of one or more data contexts.
 * 
 * Multiple providers may represent aspects of aspects of a data context (such
 * as terminology).
 * 
 * Furthermore, multiple providers may represent the same exact data context.
 */
public interface ProviderHandler extends Configurable {

  /**
   * Ensures input context is supported. If it is, returns all output contexts
   * the identify &amp; process methods support. Returns an empty list if the input
   * context is not supported.
   * 
   * 
   * Returns a list because provider may handle multiple output data contexts.
   * 
   * Note: Often overridden by AbstractAcceptsHandler.
   *
   * @param inputContext the input context
   * @return the list
   * @throws Exception the exception
   */
  public List<DataContext> accepts(DataContext inputContext) throws Exception;

  /**
   * Adds the feedback.
   * 
   * @param inputString the input string
   * @param context the context
   * @param feedbackString the feedback string
   * @param outputContext the output context
   * @throws Exception the exception
   */
  public void addFeedback(String inputString, DataContext context,
    String feedbackString, DataContext outputContext) throws Exception;

  /**
   * Removes the feedback.
   *
   * @param inputString the input string
   * @param context the context
   * @param outputContext the output context
   * @throws Exception the exception
   */
  public void removeFeedback(String inputString, DataContext context,
    DataContext outputContext) throws Exception;

  /**
   * Identifies probability of the input string being of the type defined by the
   * provider. An empty list if identify finds nothing.
   * 
   * Returns a list of ranked possible dataContexts within the provider with an
   * associated probability score.
   * 
   * Method able to handle NULL dataContexts.
   *
   * @param record the record
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredDataContext> identify(TransformRecord record)
    throws Exception;

  /**
   * Produces list of scored (by probability) results of as specified by the
   * output context for the all normalized strings based on known input context.
   * Returns an empty list if there are no processing results.
   * 
   * Returns the highest total per normalized string.
   * 
   * Returns empty list for any DataContext for which accepts returns an empty
   * list.
   *
   * @param record the record
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> process(TransformRecord record) throws Exception;

  /**
   * Returns the quality factor (0-1) for this provider.
   *
   * @return the quality
   */
  public float getQuality();

  /**
   * Returns the value used to normalize the provider's results between 0 and 1.
   *
   * @return the log base value
   */
  public float getLogBaseValue();

  /**
   * Close any open resources on application shutdown.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;

  /**
   * Indicates whether or not pre check valid is the case based on Provider
   * defined pre-processing rules.
   *
   * @param record the record
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public boolean isPreCheckValid(TransformRecord record) throws Exception;

  /**
   * Filter results after processing based on converted results based on the
   * provider's rules.
   *
   * @param providerEvidenceMap the provider evidence map
   * @param record the record
   * @return the list
   * @throws Exception the exception
   */
  public Map<String, Float> filterResults(
    Map<String, Float> providerEvidenceMap, TransformRecord record)
      throws Exception;
}