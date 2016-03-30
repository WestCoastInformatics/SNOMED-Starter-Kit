/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;
import java.util.Map;

import com.wci.tt.helpers.HasScore;
import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.Configurable;

/**
 * Generically represents an algorithm for taking a scored list of things and
 * determining where to cutoff.
 */
public interface ThresholdHandler extends Configurable {

  /**
   * Weight result for a piece of evidence by combining the raw score from the
   * provider with quality information from the normalizer and the provider.
   * 
   * Results returned will be in the range of (0,1) thanks to incorporating
   * logBaseValue.
   *
   * @param rawScore the raw score
   * @param providerQuality the provider quality
   * @param logBaseValue the log base value
   * @return the float
   */
  public float weightResult(float rawScore, float providerQuality,
    float logBaseValue);

  /**
   * Apply threshold to the input list. Each element of the output list will be
   * contained in the input list
   *
   * @param <T> the
   * @param list the list
   * @return the list
   * @throws Exception the exception
   */
  public <T extends HasScore> List<T> applyThreshold(List<T> list)
    throws Exception;

  /**
   * Aggregate results from multiple pieces of evidence into a single result.
   *
   * @param evidence the evidence
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> aggregate(
    Map<ProviderHandler, List<ScoredResult>> evidence) throws Exception;
}
