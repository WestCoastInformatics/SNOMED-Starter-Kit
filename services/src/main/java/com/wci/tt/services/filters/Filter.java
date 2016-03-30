/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.Configurable;

/**
 * Generically represents a post processing filter.
 */
public interface Filter extends Configurable {

  /**
   * Helper function for extracting only tokens with numbers.
   *
   * @param term the term
   * @return the numerics
   */
  public static Set<String> getNumericalTokens(String term) {
    // find Numeric value from term
    String str = term.replaceAll("[^0-9.,]+", " ");
    str = str.replaceAll(",", "");
    Set<String> numerics =
        new HashSet<String>(Arrays.asList(str.trim().split(" ")));
    if (numerics.size() == 1
        && numerics.iterator().next().trim().length() == 0) {
      return new HashSet<>();
    }
    return numerics;
  }

  /**
   * Indicates whether or not the pre check filter accepts the specified
   * context.
   *
   * @param context the context
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean preCheckAccepts(DataContext context) throws Exception;

  /**
   * Indicates whether or not the post check filter accepts the specified
   * context.
   *
   * @param context the context
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean postCheckAccepts(DataContext context) throws Exception;

  /**
   * Pre filter - indicates whether to continue processing the specified input
   * string.
   *
   * @param inputString the input string
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean preCheck(String inputString) throws Exception;

  /**
   * Post filter - indicates which results to keep based on a variety of results
   * coming back (and the input data).
   *
   * @param inputString the input string
   * @param normalizedTerms the normalized terms
   * @param providerEvidenceMap the provider evidence map
   * @return true, if successful
   * @throws Exception the exception
   */
  public Map<String, Float> postCheck(String inputString,
    List<ScoredResult> normalizedTerms, Map<String, Float> providerEvidenceMap)
      throws Exception;

  /**
   * Opportunity to close any open resources.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;
}
