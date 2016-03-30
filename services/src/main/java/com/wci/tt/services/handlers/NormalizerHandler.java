/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.Configurable;

/**
 * Interface responsible for normalizing an input string of customer data
 * against its normalization knowledge in hopes of simplifying the process of
 * matching content.
 * 
 * The normalizations that take place are assisted by the data context
 * associated with the input string.
 * 
 * Results returned are ranked by probability per possible transformation as
 * more than one option may be available within the normalization knowledge
 */
public interface NormalizerHandler extends Configurable {

  /**
   * Indicates whether this normalizer operates on the specified data.
   *
   * @param inputContext the input context
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean accepts(DataContext inputContext) throws Exception;

  /**
   * Mechanism for providing feedback to normalizers. What the normalizer
   * chooses to do with the feedback data (e.g. use it literally, or generalize
   * it, or support patterns, etc) is up to the implementation.
   *
   * @param inputString the input string
   * @param inputContext the input context
   * @param feedbackString the feedback string
   * @throws Exception the exception
   */
  public void addFeedback(String inputString, DataContext inputContext,
    String feedbackString) throws Exception;

  /**
   * Removes the feedback.
   *
   * @param inputString the input string
   * @param inputContext the input context
   * @throws Exception the exception
   */
  public void removeFeedback(String inputString, DataContext inputContext)
    throws Exception;

  /**
   * Returns normalized form of input string with a probability-score based on
   * context. The score range is (0-1) for all normalizers.
   * 
   * Returns an empty list if there are no normalizations.
   * 
   * Results placed in list based in ordered fashion with top score first
   * because there may be multiple way to interpret the inputStr.
   *
   * @param inputString the input string
   * @param context the context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> normalize(String inputString, DataContext context)
    throws Exception;

  /**
   * Returns the quality factor (0-1) for this normalizer.
   *
   * @return the quality
   */
  public float getQuality();

  /**
   * Close any open resources on application shutdown.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;
}
