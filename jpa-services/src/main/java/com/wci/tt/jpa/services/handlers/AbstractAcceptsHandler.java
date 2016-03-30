/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.wci.tt.DataContext;
import com.wci.tt.jpa.services.helper.DataContextMatcher;

/**
 * Abstract handler for the "accepts" method - used for ConverterHandler and
 * link ProviderHandler implementations;.
 */
public abstract class AbstractAcceptsHandler {

  /** The io matchers. */
  private Map<DataContextMatcher, List<DataContextMatcher>> ioMatchers =
      new HashMap<>();

  /** The quality. */
  private float quality;

  /**
   * Adds the matcher.
   *
   * @param inputMatcher the input matcher
   * @param outputMatcher the output matcher
   */
  public void addMatcher(DataContextMatcher inputMatcher,
    DataContextMatcher outputMatcher) {
    if (!ioMatchers.containsKey(inputMatcher)) {
      ioMatchers.put(inputMatcher, new ArrayList<DataContextMatcher>());
    }
    ioMatchers.get(inputMatcher).add(outputMatcher);
  }

  /**
   * Ensures input context is supported. If it is, returns all output contexts
   * supported.
   * 
   * Returns a list because may handle multiple output data contexts.
   *
   * @param inputContext the input context
   * @return the list
   * @throws Exception the exception
   */
  public List<DataContext> accepts(DataContext inputContext) throws Exception {
    for (final DataContextMatcher inputMatcher : ioMatchers.keySet()) {
      if (inputMatcher.matches(inputContext)) {
        final Set<DataContext> contexts = new HashSet<>();
        for (final DataContextMatcher matcher : ioMatchers.get(inputMatcher)) {
          contexts.addAll(matcher.getDataContexts());
        }
        return new ArrayList<>(contexts);
      }
    }
    return new ArrayList<>();
  }

  /**
   * Validates the input/output combination with the matcher. This is a utility
   * method for use by subclasses.
   *
   * @param inputContext the input context
   * @param outputContext the output context
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean validate(DataContext inputContext, DataContext outputContext)
    throws Exception {
    if (inputContext != null && outputContext != null) {
      for (final DataContext matchContext : accepts(inputContext)) {
        if (DataContextMatcher.matches(outputContext, matchContext)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Sets the quality - this should be called via super() if other local
   * properties are needed.
   *
   * @param p the properties
   * @throws Exception the exception
   */
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

  /**
   * Returns the quality.
   *
   * @return the quality
   */
  public float getQuality() {
    return quality;
  }
}