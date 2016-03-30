/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import java.util.List;
import java.util.Map;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.TransformRecordList;
import com.wci.tt.helpers.TypeKeyValue;
import com.wci.tt.infomodels.InfoModel;
import com.wci.tt.services.handlers.AnalyzerHandler;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.tt.services.handlers.SourceDataLoader;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.services.RootService;

/**
 * Generically represents a service for handling top-level
 * terminology-transformation routines.
 */
public interface CoordinatorService extends RootService {

  /**
   * Returns the source data loaders.
   *
   * @return the source data loaders
   * @throws Exception the exception
   */
  public Map<String, SourceDataLoader> getSourceDataLoaders() throws Exception;

  /**
   * Returns the normalizers.
   *
   * @return the normalizers
   * @throws Exception the exception
   */
  public Map<String, NormalizerHandler> getNormalizers() throws Exception;

  /**
   * Returns the analyzers.
   *
   * @return the analyzers
   * @throws Exception the exception
   */
  public Map<String, AnalyzerHandler> getAnalyzers() throws Exception;

  /**
   * Returns the providers.
   *
   * @return the providers
   * @throws Exception the exception
   */
  public Map<String, ProviderHandler> getProviders() throws Exception;

  /**
   * Returns the converters.
   *
   * @return the converters
   * @throws Exception the exception
   */
  public Map<String, ConverterHandler> getConverters() throws Exception;

  /**
   * Returns the specialties.
   *
   * @return the specialties
   * @throws Exception the exception
   */
  public List<String> getSpecialties() throws Exception;

  /**
   * Returns the semantic types.
   *
   * @return the semantic types
   * @throws Exception the exception
   */
  public List<String> getSemanticTypes() throws Exception;

  /**
   * Returns the information models.
   *
   * @return the information models
   * @throws Exception the exception
   */
  public Map<String, InfoModel<?>> getInformationModels() throws Exception;

  /**
   * Finds which data contexts are legible across all providers.
   * 
   * To do so, the method takes the input string and input data context and
   * looks at all Providers to determine their probability of being able to
   * process the input string.
   * 
   * Steps: 1) Calls accept per provider - Generates map of provider to list of
   * supported data contexts 2) Generate normalized content per accepted data
   * contexts - Generates collated List of DataContextTuple 3) Call identify for
   * each provider's accepted data context on data context's associated
   * normalized results - Generates List of ScoredDataContext
   *
   * The associated score may be examined to determine if the probability score
   * passes a statistical threshold. This includes for: - For all content
   * generated via all normalize() calls - For all content generated via all
   * identify() calls
   *
   * @param inputStr the input string
   * @param inputContext the input context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredDataContext> identify(String inputStr,
    DataContext inputContext) throws Exception;

  /**
   * Assembles the possible combinations of Providers &amp; Converters based on
   * input Context. Then normalizes content across all normalizers per data
   * context. For each Provider/Converter combination, puts pair's data context
   * and associated normalized content through process &amp; convert methods. All
   * data is returned.
   * 
   * Steps: 1) Identify contexts acceptable by Provider/Converter pairs -
   * Generates triplet (DataContext to Provider to Set of Converters) 2) Generate
   * normalized content per accepted data contexts - Generates collated List of
   * DataContextTuple 3) Calls process and convert for each accepted context on
   * data context's associated normalized results - Generates List of
   * DataContextTuples
   * 
   * The associated score may be examined to determine if the probability score
   * passes a statistical threshold. This includes for: - For all content
   * generated via all normalize() calls - For all content generated via all
   * process() calls
   * 
   * @param inputStr the input string
   * @param inputContext the input context
   * @param outputContext the output context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> process(String inputStr, DataContext inputContext,
    DataContext outputContext) throws Exception;

  /**
   * Normalize the input string by running it through all normalizers. The score
   * range is (0-1) for all normalizers.
   *
   * @param inputStr the input str
   * @param requiredInputContext the required input context
   * @param includeOriginal the include original
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> normalize(String inputStr,
    DataContext requiredInputContext, boolean includeOriginal) throws Exception;

  /**
   * Normalizer feedback.
   *
   * @param inputString the input string
   * @param inputContext the input context
   * @param feedbackString the feedback string
   * @throws Exception the exception
   */
  public void addNormalizerFeedback(String inputString,
    DataContext inputContext, String feedbackString) throws Exception;

  /**
   * Removes the normalizer feedback.
   *
   * @param inputString the input string
   * @param inputContext the input context
   * @throws Exception the exception
   */
  public void removeNormalizerFeedback(String inputString,
    DataContext inputContext) throws Exception;

  /**
   * Provider feedback.
   *
   * @param inputString the input string
   * @param inputContext the input context
   * @param feedbackString the feedback string
   * @param outputContext the output context
   * @throws Exception the exception
   */
  public void addProviderFeedback(String inputString, DataContext inputContext,
    String feedbackString, DataContext outputContext) throws Exception;

  /**
   * Removes the provider feedback.
   *
   * @param inputString the input string
   * @param inputContext the input context
   * @param outputContext the output context
   * @throws Exception the exception
   */
  public void removeProviderFeedback(String inputString,
    DataContext inputContext, DataContext outputContext) throws Exception;

  /**
   * Adds the transform record.
   *
   * @param record the record
   * @return the transform record
   * @throws Exception the exception
   */
  public TransformRecord addTransformRecord(TransformRecord record)
    throws Exception;

  /**
   * Update transform record.
   *
   * @param record the record
   * @throws Exception the exception
   */
  public void updateTransformRecord(TransformRecord record) throws Exception;

  /**
   * Removes the transform record.
   *
   * @param recordId the record id
   * @throws Exception the exception
   */
  public void removeTransformRecord(Long recordId) throws Exception;

  /**
   * Returns the transform record.
   *
   * @param recordId the record id
   * @return the transform record
   * @throws Exception the exception
   */
  public TransformRecord getTransformRecord(Long recordId) throws Exception;

  /**
   * Find transform records for query.
   *
   * @param query the query
   * @param pfs the pfs
   * @return the transform record list
   * @throws Exception the exception
   */
  public TransformRecordList findTransformRecordsForQuery(String query,
    PfsParameter pfs) throws Exception;

  /**
   * Adds the type, key, value.
   *
   * @param typeKeyValue the type key value
   * @return the type, key, value
   * @throws Exception the exception
   */
  public TypeKeyValue addTypeKeyValue(TypeKeyValue typeKeyValue)
    throws Exception;

  /**
   * Update type, key, value.
   *
   * @param typeKeyValue the type key value
   * @throws Exception the exception
   */
  public void updateTypeKeyValue(TypeKeyValue typeKeyValue) throws Exception;

  /**
   * Removes the type, key, value.
   *
   * @param typeKeyValueId the type key value id
   * @throws Exception the exception
   */
  public void removeTypeKeyValue(Long typeKeyValueId) throws Exception;

  /**
   * Returns the type, key, value.
   *
   * @param typeKeyValueId the type key value id
   * @return the type, key, value
   * @throws Exception the exception
   */
  public TypeKeyValue getTypeKeyValue(Long typeKeyValueId) throws Exception;

  /**
   * Find type, key, values for query.
   *
   * @param query the query
   * @return the type, key, value list
   * @throws Exception the exception
   */
  public List<TypeKeyValue> findTypeKeyValuesForQuery(String query)
    throws Exception;

  /**
   * Used to close handlers before shutting application down.
   *
   * @throws Exception the exception
   */
  public void closeHandlers() throws Exception;

}
