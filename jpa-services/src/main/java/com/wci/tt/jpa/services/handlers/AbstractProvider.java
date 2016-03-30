/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.infomodels.InfoModel;
import com.wci.tt.services.filters.Filter;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.services.handlers.SearchHandler;

/**
 * Abstract implementation of {@link ProviderHandler}.
 * 
 * Provides utilities for key algorithms:
 * 
 * <pre>
 * 1. Process 
 * 2. Identify (yet to be implemented)
 * </pre>
 */
public abstract class AbstractProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /** The pre process filter. */
  private List<Filter> preProcessFilters = null;

  /** The post process filter. */
  private List<Filter> postProcessFilters = null;

  /** The log base value. */
  private float LOG_BASE_VALUE = 0f;

  /** The info model. */
  private InfoModel<?> infoModel = null;;

  /**
   * Instantiates an empty {@link AbstractProvider}.
   */
  public AbstractProvider() {
    // n/a
  }

  /* see superclass */
  @Override
  public boolean isPreCheckValid(TransformRecord record) throws Exception {
    // Filter precheck on the term name
    for (final Filter filter : getPreProcessFilters()) {
      if (!filter.preCheckAccepts(record.getInputContext())
          || !filter.preCheck(record.getInputString())) {
        return false;
      }
    }

    return true;
  }

  /* see superclass */
  @Override
  public Map<String, Float> filterResults(
    Map<String, Float> providerEvidenceMap, TransformRecord record)
      throws Exception {
    Map<String, Float> results = providerEvidenceMap;
    for (final Filter filter : getPostProcessFilters()) {
      if (filter.postCheckAccepts(record.getOutputContext())) {
        results = filter.postCheck(record.getInputString(),
            record.getNormalizedResults(), results);
      }
    }
    return results;
  }

  /**
   * Returns the provider-specific info model as created from the input string.
   *
   * @param inputString the input string
   * @return the info model
   * @throws Exception the exception
   */
  public abstract InfoModel<?> getModelForString(String inputString)
    throws Exception;

  /**
   * Indicates whether or not term is supported based on the provider.
   * 
   * Note: Many cases will simply return true, but some cases (like
   * distinguishing between LOINC vs regular LAB records may utilize this
   * approach)
   *
   * @param model the model
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public abstract boolean isTermSupported(InfoModel<?> model) throws Exception;

  /**
   * Perform actual provider-specific search. Can be used by providers that want
   * to handle individual normalized terms, then limit overall results at the
   * end. See the local process method.
   *
   * @param inputString the input string
   * @param handler the handler
   * @param service the service
   * @param version the version
   * @return the list
   * @throws Exception the exception
   */
  public abstract List<ScoredResult> performSearch(String inputString,
    SearchHandler handler, ContentServiceJpa service, String version)
      throws Exception;

  /**
   * Limit results based on some notion of local quality. Default is to not
   * limit results
   *
   * @param results the results
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> limitResults(List<ScoredResult> results)
    throws Exception {
    return results;
  }

  /* see superclass */
  public List<ScoredResult> process(TransformRecord record) throws Exception {
    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext providerOutputContext = record.getProviderOutputContext();

    // Validate input/output context and inputString
    if (!validate(inputContext, providerOutputContext) || inputString == null
        || inputString.isEmpty()) {
      return new ArrayList<>();
    }

    Logger.getLogger(getClass()).debug("Process input term = " + inputString);
    Logger.getLogger(getClass()).debug("  inputContext = " + inputContext);
    Logger.getLogger(getClass())
        .debug("  providerOutputContext = " + providerOutputContext);

    final ContentServiceJpa service = new ContentServiceJpa();
    final SearchHandler searchHandler =
        service.getSearchHandler(ConfigUtility.DEFAULT);

    // Get version from TermRecord if defined in either its
    // providerOutputContext or inputContext
    final String version = (providerOutputContext.getVersion() != null)
        ? providerOutputContext.getVersion() : inputContext.getVersion();

    try {
      Map<String, ScoredResult> allResults = new HashMap<>();

      for (ScoredResult normResult : record.getValuesToProcess()) {
        infoModel = getModelForString(normResult.getValue());

        // Bail if the provider doesn't support this model
        // e.g., skip messy cases
        if (!isTermSupported(infoModel)) {
          return new ArrayList<>();
        }

        List<ScoredResult> results = performSearch(normResult.getValue(),
            searchHandler, service, version);

        /* Log Results */
        Logger.getLogger(getClass()).info(
            "  results for " + normResult.getValue() + " = " + results.size());
        for (final ScoredResult result : results) {
          // Multiple process score by the normalized score to help identify top
          // scores to return when there are more than one instance of a concept
          // matching on different normalized strings
          result.setScore(result.getScore() * normResult.getScore());

          Logger.getLogger(getClass()).info(
              "    concept = (" + result.getScore() + ") " + result.getValue());

          if (!allResults.containsKey(result.getValue()) || (allResults
              .get(result.getValue()).getScore() < result.getScore())) {
            allResults.put(result.getValue(), result);
          }
        }
      }

      return produceFinalResults(allResults);
    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }

  }

  /**
   * <pre>
   * Method to handle the finalization of the return ScoredResult(s):
   * 1) Prints out the results of the combined process calls (per normalized string).
   * 2) Executes the already existing limitResults() call.
   * 3) Prints out the final results returned to the Coordinator.
   * </pre>
   *
   * @param allResults the all results
   * @return the list
   * @throws Exception the exception
   */
  private List<ScoredResult> produceFinalResults(
    Map<String, ScoredResult> allResults) throws Exception {
    final ArrayList<ScoredResult> currentResults =
        new ArrayList<ScoredResult>(allResults.values());
    Collections.sort(currentResults);

    Logger.getLogger(getClass()).info("Combined Results");
    for (final ScoredResult result : currentResults) {
      Logger.getLogger(getClass()).info(
          "    result = (" + result.getScore() + ") " + result.getValue());
    }

    final List<ScoredResult> finalResults = limitResults(currentResults);

    Logger.getLogger(getClass()).info("Final Results");
    for (final ScoredResult result : finalResults) {
      Logger.getLogger(getClass()).info(
          "    result = (" + result.getScore() + ") " + result.getValue());
    }

    return finalResults;
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(TransformRecord record)
    throws Exception {
    throw new UnsupportedOperationException(
        "No need for identify at this time");
  }

  /* see superclass */
  @Override
  public float getLogBaseValue() {
    return LOG_BASE_VALUE;
  }

  /**
   * Sets the log base value.
   *
   * @param value the log base value
   */
  public void setLogBaseValue(float value) {
    LOG_BASE_VALUE = value;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    for (final Filter filter : getPreProcessFilters()) {
      filter.close();
    }

    for (final Filter filter : getPostProcessFilters()) {
      filter.close();
    }
  }

  /**
   * Returns the pre process filters.
   *
   * @return the pre process filters
   */
  public List<Filter> getPreProcessFilters() {
    if (preProcessFilters == null) {
      preProcessFilters = new ArrayList<>();
    }
    return preProcessFilters;
  }

  /**
   * Sets the pre process filters.
   *
   * @param preProcessFilters the pre process filters
   */
  public void setPreProcessFilters(List<Filter> preProcessFilters) {
    this.preProcessFilters = preProcessFilters;
  }

  /**
   * Returns the post process filters.
   *
   * @return the post process filters
   */
  public List<Filter> getPostProcessFilters() {
    if (postProcessFilters == null) {
      postProcessFilters = new ArrayList<>();
    }
    return postProcessFilters;
  }

  /**
   * Sets the post process filters.
   *
   * @param postProcessFilters the post process filters
   */
  public void setPostProcessFilters(List<Filter> postProcessFilters) {
    this.postProcessFilters = postProcessFilters;
  }

  /**
   * Returns the info model.
   *
   * @return the info model
   */
  public InfoModel<?> getInfoModel() {
    return infoModel;
  }

  /**
   * Sets the info model.
   *
   * @param infoModel the info model
   */
  public void setInfoModel(InfoModel<?> infoModel) {
    this.infoModel = infoModel;
  }

  /* see superclass */
  @Override
  public void addFeedback(String inputString, DataContext context,
    String feedbackString, DataContext outputContext) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void removeFeedback(String inputString, DataContext context,
    DataContext outputContext) throws Exception {
    // n/a
  }
}
