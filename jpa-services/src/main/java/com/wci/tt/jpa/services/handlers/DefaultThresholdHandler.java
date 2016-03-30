/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.wci.tt.helpers.HasScore;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.tt.services.handlers.ThresholdHandler;

/**
 * Default implementation of {@link ThresholdHandler}. This implementation
 * simply returns everything.
 */
public class DefaultThresholdHandler implements ThresholdHandler {

  /* see superclass */
  @Override
  public String getName() {
    return "Default Threshold Handler";
  }

  /* see superclass */
  @Override
  public void setProperties(Properties arg0) throws Exception {
    // n/a
    // thought this is a good opportunity to pass in a threshold value
  }

  /* see superclass */
  @Override
  public <T extends HasScore> List<T> applyThreshold(List<T> list)
    throws Exception {
    // apply no threshold, just pass everything through
    return list;
  }

  /* see superclass */
  @Override
  public List<ScoredResult> aggregate(
    Map<ProviderHandler, List<ScoredResult>> evidence) throws Exception {
    final List<ScoredResult> results = new ArrayList<>();

    // Just return the highest score - no extra weight for multiple providers.
    // Scores have already been weighted by provider quality
    final Map<String, ScoredResult> scoreMap = new HashMap<>();
    for (final ProviderHandler provider : evidence.keySet()) {
      for (final ScoredResult result : evidence.get(provider)) {
        if (!scoreMap.containsKey(result.getValue())
            || scoreMap.get(result.getValue()).getScore() < result.getScore()) {
          scoreMap.put(result.getValue(), result);
        }
      }
    }
    // Now convert scoreMap back into a scored result list - which will be
    // sorted later
    for (final String key : scoreMap.keySet()) {
      results.add(scoreMap.get(key));
    }
    return results;
  }

  /* see superclass */
  @Override
  public float weightResult(float rawScore, float providerQuality,
    float logBaseValue) {

    // Weight all parts (for now)
    final float score = rawScore * providerQuality;

    if (logBaseValue <= 0) {
      return score;
    } else if (score >= logBaseValue) {
      return 1;
    } else {
      return score / logBaseValue;
    }
  }

}
