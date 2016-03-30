/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.services.handlers.NormalizerHandler;

/**
 * Default implementation of {@link NormalizerHandler}.
 * 
 * This class demonstrates a "naive" implementation of the Normalizer.
 * 
 * Class created to prove that supporting functionality works, not to provide
 * meaningful results.
 */
public class DefaultNormalizer extends AbstractNormalizer
    implements NormalizerHandler {

  /** The quality. */
  private float quality;

  /**
   * Instantiates an empty {@link DefaultNormalizer}.
   */
  public DefaultNormalizer() {
    // n/a
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Default Normalizer Handler";
  }

  /* see superclass */
  @Override
  public List<ScoredResult> normalize(String inputStr, DataContext context)
    throws Exception {
    // Simply return data passed in for this "naive" case. As such, the score is
    // set to '1'.
    List<ScoredResult> results = new ArrayList<ScoredResult>();

    // Ensure that input is valid.
    if (inputStr != null && !inputStr.isEmpty() && context != null) {
      ScoredResult r = new ScoredResultJpa();
      r.setValue(inputStr);
      r.setScore(1);
      results.add(r);
    }

    return results;
  }

  /* see superclass */
  public void setProperties(Properties p) throws Exception {
    if (p == null) {
      throw new Exception("A quality property is required");
    }
    if (!p.containsKey("quality")) {
      throw new Exception("A quality property is required");
    }

    try {
      quality = Float.parseFloat(p.getProperty("quality"));
      if (quality < 0 || quality > 1) {
        throw new Exception();
      }
    } catch (Exception e) {
      throw new Exception(
          "quality property must be a float value between 0 and 1");
    }
  }

  /* see superclass */
  @Override
  public float getQuality() {
    return quality;
  }

  /* see superclass */
  @Override
  public boolean accepts(DataContext inputContext) throws Exception {
    return true;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }
}
