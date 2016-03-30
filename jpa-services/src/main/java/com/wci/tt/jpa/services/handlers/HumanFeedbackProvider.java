/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.QueryParserBase;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.TransformRecordList;
import com.wci.tt.jpa.TransformRecordJpa;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;

/**
 * Represents a {@link ProviderHandler} based entirely on human feedback. It
 * does not generalize at the moment, simply looks up the corresponding input
 * and returns the corresponding output.
 */
public class HumanFeedbackProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /** The override. */
  private boolean override = true;

  /**
   * Instantiates an empty {@link HumanFeedbackProvider}.
   *
   * @throws Exception the exception
   */
  public HumanFeedbackProvider() throws Exception {

    // Configure input/output matchers
    DataContextMatcher matcher1 = new DataContextMatcher();
    matcher1.configureContext(DataContextType.CODE, null, null, null, null,
        null, null);
    DataContextMatcher matcher2 = new DataContextMatcher();
    matcher2.configureContext(DataContextType.NAME, null, null, null, null,
        null, null);
    DataContextMatcher matcher3 = new DataContextMatcher();
    matcher3.configureContext(DataContextType.INFO_MODEL, null, null, null,
        null, null, null);

    // produce same output as for input
    addMatcher(matcher1, matcher1);
    addMatcher(matcher1, matcher2);
    addMatcher(matcher2, matcher1);
    addMatcher(matcher1, matcher3);
    addMatcher(matcher3, matcher1);
    addMatcher(matcher3, matcher3);
    addMatcher(matcher2, matcher2);
    addMatcher(matcher2, matcher3);
    addMatcher(matcher3, matcher2);
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Human Feedback Provider";
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(TransformRecord record)
    throws Exception {
    // does not identify
    return new ArrayList<ScoredDataContext>();
  }

  /* see superclass */
  @Override
  public List<ScoredResult> process(TransformRecord record) throws Exception {

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext providerOutputContext = record.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, providerOutputContext);

    // Simply return data passed in for this "naive" case. As such, the score is
    // set to '1'.
    final List<ScoredResult> results = new ArrayList<ScoredResult>();
    final CoordinatorService service = new CoordinatorServiceJpa();
    try {

      // Look up the input string and input context
      // for this owner - check the output type, return it
      final String query = "lastModifiedBy:" + getClass().getName() + " AND "
          + "inputStringSort:\"" + QueryParserBase.escape(inputString) + "\"";
      // Only look up first results
      final PfsParameter pfs = new PfsParameterJpa();
      pfs.setStartIndex(0);
      pfs.setMaxResults(1);
      Logger.getLogger(getClass()).info("  query = " + query);
      final TransformRecordList list =
          service.findTransformRecordsForQuery(query, pfs);
      Logger.getLogger(getClass()).info("  list = " + list);

      if (list.getTotalCount() > 1) {
        Logger.getLogger(getClass()).error("  list = " + list);
        throw new LocalException(
            "Unexpected number of results for feedback input string");
      }

      // Get the transform record
      final TransformRecord result = list.getObjects().get(0);

      // Validate the input/output contexts
      validate(result.getInputContext(), providerOutputContext);
      validate(inputContext, result.getOutputContext());

      // Return the output string with high score
      results.addAll(result.getOutputs());
      Logger.getLogger(getClass()).info("  results = " + results.size());
      Logger.getLogger(getClass()).info("          = " + results);
      return results;

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    super.setProperties(p);

    if (p.containsKey("override")) {
      override = "true".equals(p.getProperty("override").toString());
    }
  }

  /* see superclass */
  @Override
  public void addFeedback(String inputString, DataContext context,
    String feedbackString, DataContext outputContext) throws Exception {
    final CoordinatorService service = new CoordinatorServiceJpa();
    try {

      final String query = "lastModifiedBy:" + getClass().getName() + " AND "
          + "inputStringSort:\"" + QueryParserBase.escape(inputString) + "\"";
      // Only look up first results
      final PfsParameter pfs = new PfsParameterJpa();
      pfs.setStartIndex(0);
      pfs.setMaxResults(1);
      TransformRecordList list =
          service.findTransformRecordsForQuery(query, pfs);
      if (list.getTotalCount() > 0) {

        // If override is set, replace it
        if (override) {
          service.removeTransformRecord(list.getObjects().get(0).getId());
        }

        // otherwise, throw an exception
        else {
          throw new LocalException(
              "Feedback was supplied for a value that already has "
                  + "feedback with an override flag set to false.");
        }
      }

      TransformRecord record = new TransformRecordJpa();
      record.setLastModifiedBy(getClass().getName());
      record.setInputString(inputString);
      record.setInputContext(context);
      record.setOutputContext(outputContext);
      record.getOutputs().add(new ScoredResultJpa(feedbackString, 1000));
      Logger.getLogger(getClass()).info("  add feedback record = " + record);
      service.addTransformRecord(record);

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  public void removeFeedback(String inputString, DataContext context,
    DataContext outputContext) throws Exception {
    final CoordinatorService service = new CoordinatorServiceJpa();
    try {

      final String query =
          "inputString:\"" + QueryParserBase.escape(inputString) + "\"";
      // Only look up first results
      final TransformRecordList list =
          service.findTransformRecordsForQuery(query, null);
      for (final TransformRecord record : list.getObjects()) {
        service.removeTransformRecord(record.getId());
      }

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  public float getLogBaseValue() {
    // n/a
    return 0;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }

  /* see superclass */
  @Override
  public boolean isPreCheckValid(TransformRecord record) {
    // Initial setup until specific rules defined
    return true;
  }

  /* see superclass */
  @Override
  public Map<String, Float> filterResults(
    Map<String, Float> providerEvidenceMap, TransformRecord record) {
    // Initial setup until specific rules defined
    return providerEvidenceMap;
  }
}
