/*

 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.TransformRecordList;
import com.wci.tt.helpers.TypeKeyValue;
import com.wci.tt.infomodels.InfoModel;
import com.wci.tt.jpa.TransformRecordJpa;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.helpers.TransformRecordListJpa;
import com.wci.tt.jpa.helpers.TypeKeyValueJpa;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.AnalyzerHandler;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.tt.services.handlers.SourceDataLoader;
import com.wci.tt.services.handlers.ThresholdHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.services.handlers.SearchHandler;

/**
 * JPA and JAXB-enabled implementation of {@link CoordinatorService}.
 */
public class CoordinatorServiceJpa extends ContentServiceJpa
    implements CoordinatorService {

  /** The is analysis run. */
  private static boolean isAnalysisRun = false;

  /** The config properties. */
  protected static Properties config = null;

  /** The threshold. */
  static ThresholdHandler threshold = null;

  /** The source data loaders. */
  static Map<String, SourceDataLoader> loaders = new HashMap<>();

  /** The normalizer handler . */
  static Map<String, NormalizerHandler> normalizers = new HashMap<>();

  /** The normalizer handler . */
  static Map<String, AnalyzerHandler> analyzers = new HashMap<>();

  /** The provider handler . */
  static Map<String, ProviderHandler> providers = new HashMap<>();

  /** The converter handler . */
  static Map<String, ConverterHandler> converters = new HashMap<>();

  /** The information models. */
  static Map<String, InfoModel<?>> infoModels = new HashMap<>();

  /** The config-specified specialties available. */
  static List<String> specialties = new ArrayList<>();

  /** The config-specified semantic types available. */
  static List<String> semanticTypes = new ArrayList<>();

  static {
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      if (config.containsKey("execution.type.analysis")) {
        isAnalysisRun = Boolean.parseBoolean(ConfigUtility.getConfigProperties()
            .getProperty("execution.type.analysis"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    /** threshold handler - only one */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "threshold.handler";
      String handlerName = config.getProperty(key);
      // Add handlers to List
      threshold = ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
          handlerName, ThresholdHandler.class);

      if (threshold == null)
        throw new Exception(
            "threshold.handler must have exactly one value but none exists");
    } catch (Exception e) {
      e.printStackTrace();
      threshold = null;
    }

    /** Configure loaders */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "source.data.loader.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        SourceDataLoader handler =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, SourceDataLoader.class);
        loaders.put(handlerName, handler);
      }
      if (loaders.isEmpty()) {
        throw new Exception(
            "source.data.loader.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      loaders = null;
    }

    /** Add normalizers found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "normalizer.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        NormalizerHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, NormalizerHandler.class);
        normalizers.put(handlerName, handlerService);
      }
      if (normalizers.isEmpty()) {
        throw new Exception(
            "normalizer.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      normalizers = null;
    }

    /** Add analyzers found in config. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "analyzer.handler";
      // Not required
      if (config.containsKey(key)) {
        for (String handlerName : config.getProperty(key).split(",")) {
          if (handlerName.isEmpty()) {
            continue;
          }
          // Add handlers to List
          AnalyzerHandler handlerService =
              ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                  handlerName, AnalyzerHandler.class);
          analyzers.put(handlerName, handlerService);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      analyzers = null;
    }

    /** Add providers found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "provider.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        ProviderHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ProviderHandler.class);
        providers.put(handlerName, handlerService);
      }
      if (providers.isEmpty()) {
        throw new Exception(
            "provider.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      providers = null;
    }

    /** Add converters found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "converter.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        ConverterHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ConverterHandler.class);
        converters.put(handlerName, handlerService);
      }
      if (converters.isEmpty()) {
        throw new Exception(
            "converter.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      converters = null;
    }

    /** Add information Models found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "info.model";
      for (String informationModel : config.getProperty(key).split(",")) {
        if (informationModel.isEmpty()) {
          continue;
        }
        // Add handlers to List
        InfoModel<?> model =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                informationModel, InfoModel.class);

        infoModels.put(informationModel, model);
      }
    } catch (Exception e) {
      e.printStackTrace();
      infoModels = null;
    }

    /** Add specialties found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "specialties.available";
      for (String specialty : config.getProperty(key).split(";")) {
        if (specialty.isEmpty()) {
          continue;
        }
        specialties.add(specialty);
      }
    } catch (Exception e) {
      e.printStackTrace();
      specialties = null;
    }

    /** Add semanticTypes found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "semanticTypes.available";
      for (String semanticType : config.getProperty(key).split(";")) {
        if (semanticType.isEmpty()) {
          continue;
        }
        semanticTypes.add(semanticType);
      }
    } catch (Exception e) {
      e.printStackTrace();
      semanticTypes = null;
    }
  }

  /**
   * Instantiates an empty {@link CoordinatorServiceJpa}.
   *
   * @throws Exception the exception
   */
  public CoordinatorServiceJpa() throws Exception {
    super();

    if (normalizers == null) {
      throw new Exception(
          "Normalizer Handlers did not properly initialize, serious error.");
    }

    if (providers == null) {
      throw new Exception(
          "Provider Handlers did not properly initialize, serious error.");
    }

    if (converters == null) {
      throw new Exception(
          "Converter Handlers did not properly initialize, serious error.");
    }

    if (infoModels == null) {
      throw new Exception(
          "The Information Models did not properly initialize, serious error.");
    }

    if (specialties == null) {
      throw new Exception(
          "The Available Specialties list did not properly initialize, serious error.");
    }

    if (semanticTypes == null) {
      throw new Exception(
          "The Available Semantic Types list did not properly initialize, serious error.");
    }
  }

  /* see superclass */
  @Override
  public Map<String, NormalizerHandler> getNormalizers() throws Exception {
    Logger.getLogger(getClass()).debug("Get normalizers");
    Logger.getLogger(getClass()).debug("  normalizers = " + normalizers);
    return normalizers;
  }

  /* see superclass */
  @Override
  public Map<String, AnalyzerHandler> getAnalyzers() throws Exception {
    Logger.getLogger(getClass()).debug("Get analyzers");
    Logger.getLogger(getClass()).debug("  analyzers = " + analyzers);
    return analyzers;
  }

  /* see superclass */
  @Override
  public Map<String, ProviderHandler> getProviders() throws Exception {
    Logger.getLogger(getClass()).debug("Get providers");
    Logger.getLogger(getClass()).debug("  providers = " + providers);
    return providers;
  }

  /* see superclass */
  @Override
  public Map<String, ConverterHandler> getConverters() throws Exception {
    Logger.getLogger(getClass()).debug("Get converters");
    Logger.getLogger(getClass()).debug("  converters = " + converters);
    return converters;
  }

  /* see superclass */
  @Override
  public List<String> getSpecialties() throws Exception {
    Logger.getLogger(getClass()).debug("Get specialties");
    Logger.getLogger(getClass()).debug("  specialties = " + specialties);
    return specialties;
  }

  /* see superclass */
  @Override
  public List<String> getSemanticTypes() throws Exception {
    Logger.getLogger(getClass()).debug("Get semantic types");
    Logger.getLogger(getClass()).debug("  semantic types s= " + semanticTypes);
    return semanticTypes;
  }

  /* see superclass */
  @Override
  public Map<String, InfoModel<?>> getInformationModels() throws Exception {
    Logger.getLogger(getClass()).debug("Get information models");
    Logger.getLogger(getClass()).debug("  models = " + infoModels);
    return infoModels;
  }

  /* see superclass */
  @Override
  public Map<String, SourceDataLoader> getSourceDataLoaders() throws Exception {
    Logger.getLogger(getClass()).debug("Get source data loaders");
    Logger.getLogger(getClass()).debug("  loaders = " + loaders);
    return loaders;
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(String inputStr,
    DataContext requiredInputContext) throws Exception {
    Logger.getLogger(getClass())
        .debug("Identify - " + inputStr + ", " + requiredInputContext);
    final List<ScoredDataContext> allIdentifiedResults = new ArrayList<>();

    // Nothing identified
    if (inputStr == null || inputStr.isEmpty()) {
      return new ArrayList<>();
    }

    // STEP 1: Call accept per provider: Generates map of provider to list of
    // supported data contexts
    final List<ProviderHandler> providers =
        getIdentifyProviders(requiredInputContext);
    Logger.getLogger(getClass()).debug("  providers = " + providers);

    // STEPS 2: Generate normalized content per accepted data contexts
    // All output data context tuples should be compatible with providers list
    final List<ScoredResult> normalizedResults =
        normalize(inputStr, requiredInputContext, false);

    final TransformRecord record = new TransformRecordJpa();
    record.setInputString(inputStr);
    record.setInputContext(requiredInputContext);
    record.setNormalizedResults(normalizedResults);

    // STEP 3: Call identify per each provider's accepted data contexts on
    // data context's associated normalized results
    final Map<ProviderHandler, List<ScoredDataContext>> allEvidenceMap =
        new HashMap<>();
    for (final ProviderHandler provider : providers) {
      // Evidence from this provider
      final Map<ScoredDataContext, Float> providerEvidenceMap = new HashMap<>();
      // normalize input
      // identify normalized input
      for (final ScoredDataContext identifiedResult : provider
          .identify(record)) {

        if (identifiedResult != null) {
          // Compute the score for this piece of evidence
          // Weight by provider quality
          // Weight by normalized input score
          final float score =
              threshold.weightResult(identifiedResult.getScore(),
                  provider.getQuality(), provider.getLogBaseValue());

          // Put in providerEvidenceMap if we don't have an entry yet
          // or this one has a higher score.
          Logger.getLogger(getClass())
              .debug("  evidence = " + provider.getName() + ", " + ", " + score
                  + ", " + identifiedResult);

          if (!providerEvidenceMap.containsKey(identifiedResult)
              || providerEvidenceMap.get(identifiedResult) < score) {
            providerEvidenceMap.put(identifiedResult, score);
          }
        }

      }

      // Add evidence from this provider to the overall list
      allEvidenceMap.put(provider,
          new ArrayList<>(providerEvidenceMap.keySet()));
    }

    // Apply threshold and return the results
    final List<ScoredDataContext> list =
        threshold.applyThreshold(allIdentifiedResults);
    Logger.getLogger(getClass()).debug("  threshold results = " + list);
    return list;
  }

  /* see superclass */
  @Override
  public List<ScoredResult> process(String inputStr, DataContext inputContext,
    DataContext requiredOutputContext) throws Exception {
    Logger.getLogger(getClass()).debug("Process - " + inputStr + ", "
        + inputContext + ", " + requiredOutputContext);

    // no processors
    if (inputStr == null || inputStr.isEmpty() || requiredOutputContext == null
        || requiredOutputContext.isEmpty()) {
      Logger.getLogger(getClass()).debug("  NO RESULTS");
      return new ArrayList<>();
    }

    boolean processedResultsFound = false;

    // STEP 1: Identify providers/converters from input context to output
    // context.
    final Map<ProviderHandler, List<DataContext>> providerMap =
        getProcessProviders(inputContext);

    // Collect provider output contexts from the provider map
    final Set<DataContext> providerOutputContexts = new HashSet<>();
    for (final ProviderHandler provider : providerMap.keySet()) {
      providerOutputContexts.addAll(providerMap.get(provider));
    }

    // Step 2: Identify the Converters that handle the provider output
    // contexts (as inputs)
    // && whose output context contains the requested outputContext
    final Map<DataContext, List<ConverterHandler>> converterMap =
        new HashMap<>();
    for (final DataContext converterInputContext : providerOutputContexts) {
      final List<ConverterHandler> converters =
          getConverters(converterInputContext, requiredOutputContext);
      converterMap.put(converterInputContext, converters);
    }

    // Step 3: Generate normalized content per accepted data contexts
    List<ScoredResult> normalizedResults =
        normalize(inputStr, inputContext, false);

    final TransformRecord record = new TransformRecordJpa();
    record.setInputString(inputStr);
    record.setInputContext(inputContext);
    record.setNormalizedResults(normalizedResults);
    record.setOutputContext(requiredOutputContext);

    // Step 4: Process and collate the results
    final Map<ProviderHandler, List<ScoredResult>> allEvidenceMap =
        new HashMap<>();
    // for each provider
    for (final ProviderHandler provider : providerMap.keySet()) {

      // Evidence from this provider
      Map<String, Float> providerEvidenceMap = new HashMap<>();

      // for each output context it generates
      for (final DataContext outputContext : providerMap.get(provider)) {
        // Set the provider output context for validation purposes
        record.setProviderOutputContext(outputContext);

        // for each supported converter
        for (final ConverterHandler converter : converterMap
            .get(outputContext)) {

          // Use provider-based filters restrict processing to be executed
          // strictly upon valid input
          if (provider.isPreCheckValid(record)) {
            // Get the initial results from the provider
            List<ScoredResult> processedResults = provider.process(record);

            if (processedResults != null && !processedResults.isEmpty()) {
              processedResultsFound = true;

              // Obtain the processed results
              for (final ScoredResult result : processedResults) {

                // Obtain the final product
                final DataContextTuple tuple =
                    converter.convert(result.getValue(), outputContext,
                        requiredOutputContext, inputStr, inputContext);

                if (tuple != null) {
                  // Compute the score for this piece of evidence
                  // Weight by provider quality
                  // Weight by normalized input score
                  float score = threshold.weightResult(result.getScore(),
                      provider.getQuality(), provider.getLogBaseValue());

                  // Put in providerEvidenceMap if we don't have an entry yet
                  // or this one has a higher score.
                  Logger.getLogger(getClass())
                      .debug("  evidence = " + score + ", " + tuple.getData());

                  if (!providerEvidenceMap.containsKey(tuple.getData())
                      || providerEvidenceMap.get(tuple.getData()) < score) {
                    providerEvidenceMap.put(tuple.getData(), score);
                  }
                }
              }

              // Having the provider's results converted, filter out content
              // that is invalid based on either input string or based on the
              // full providerEvidenceMap
              providerEvidenceMap =
                  provider.filterResults(providerEvidenceMap, record);

            } // end process
          } // end converter map
        } // end intermediate output context
      } // end precheck valid check

      // Add evidence from this provider to the overall list
      final List<ScoredResult> providerResults = new ArrayList<>();
      for (final String key : providerEvidenceMap.keySet()) {
        final ScoredResult providerResult = new ScoredResultJpa();
        providerResult.setValue(key);
        providerResult.setScore(providerEvidenceMap.get(key));
        providerResults.add(providerResult);
      }
      allEvidenceMap.put(provider, providerResults);

    }

    if (processedResultsFound) {
      // Now aggregate the evidence across all providers and sort
      List<ScoredResult> aggregatedResults =
          threshold.aggregate(allEvidenceMap);
      Collections.sort(aggregatedResults);
      Logger.getLogger(getClass())
          .debug("  final evidence = " + aggregatedResults);

      record.setOutputs(aggregatedResults);
      return aggregatedResults;
    } else {
      if (isAnalysisRun) {
        return null;
      } else {
        return new ArrayList<>();
      }
    }
  }

  /* see superclass */
  @Override
  public List<ScoredResult> normalize(String inputStr,
    DataContext requiredInputContext, boolean includeOrig) throws Exception {
    Logger.getLogger(getClass())
        .debug("Normalize - " + inputStr + ", " + requiredInputContext);
    List<ScoredResult> normalizedResults = new ArrayList<>();

    // STEP 1: Normalize input per normalizer
    final Map<String, Float> scoreMap = new HashMap<>();
    for (final NormalizerHandler normalizer : getNormalizers().values()) {

      // Get the max score for each value of the various normalizers
      for (final ScoredResult result : normalizer.normalize(inputStr,
          requiredInputContext)) {
        // Retain highest score per value
        final float score = result.getScore() * normalizer.getQuality();
        if (!scoreMap.containsKey(result.getValue())
            || scoreMap.get(result.getValue()) < score) {
          // Weight score by normalizer quality
          scoreMap.put(result.getValue(), score);
        }
      }
    }

    // Add original data if desired
    if (includeOrig) {
      scoreMap.put(inputStr, 1.0f);
    }

    // Put scoreMap into normalizedResults
    for (final String key : scoreMap.keySet()) {
      final ScoredResult result = new ScoredResultJpa();
      result.setValue(key);
      result.setScore(scoreMap.get(key));

      normalizedResults.add(result);
    }

    // Apply threshold to scores and return
    normalizedResults = threshold.applyThreshold(normalizedResults);
    return normalizedResults;
  }

  /* see superclass */
  @Override
  public void addNormalizerFeedback(String inputString,
    DataContext inputContext, String feedbackString) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Add normalizer feedback - " + inputString + ", " + inputContext);
    Logger.getLogger(getClass()).debug("  feedback = " + feedbackString);
    // Iterate through all normalizers and provide them the feedback
    for (final NormalizerHandler normalizer : getNormalizers().values()) {
      normalizer.addFeedback(inputString, inputContext, feedbackString);
    }
  }

  /* see superclass */
  @Override
  public void removeNormalizerFeedback(String inputString,
    DataContext inputContext) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Remove normalizer feedback - " + inputString + ", " + inputContext);

    // Iterate through all normalizers and provide them the feedback
    for (final NormalizerHandler normalizer : getNormalizers().values()) {
      normalizer.removeFeedback(inputString, inputContext);
    }
  }

  /* see superclass */
  @Override
  public void addProviderFeedback(String inputString, DataContext inputContext,
    String feedbackString, DataContext outputContext) throws Exception {
    Logger.getLogger(getClass())
        .debug("Add provider feedback - " + inputString + ", " + inputContext);
    Logger.getLogger(getClass())
        .debug("  feedback = " + feedbackString + ", " + outputContext);

    // Iterate through all providers and provide them the feedback
    for (final ProviderHandler provider : getProviders().values()) {
      provider.addFeedback(inputString, inputContext, feedbackString,
          outputContext);
    }

  }

  /* see superclass */
  @Override
  public void removeProviderFeedback(String inputString,
    DataContext inputContext, DataContext outputContext) throws Exception {
    Logger.getLogger(getClass()).debug("Remove provider feedback - "
        + inputString + ", " + inputContext + ", " + outputContext);

    // Iterate through all providers and provide them the feedback
    for (final ProviderHandler provider : getProviders().values()) {
      provider.removeFeedback(inputString, inputContext, outputContext);
    }

  }

  /**
   * Returns the supported provider contexts. Returns an empty list if there are
   * no supported providers.
   *
   * @param inputContext the input context
   * @return the supported provider contexts
   * @throws Exception the exception
   */
  private List<ProviderHandler> getIdentifyProviders(DataContext inputContext)
    throws Exception {
    final List<ProviderHandler> providers = new ArrayList<>();

    // Ask each provider if it accepts
    for (final ProviderHandler provider : getProviders().values()) {
      final List<DataContext> supportedContexts =
          provider.accepts(inputContext);
      if (supportedContexts == null) {
        throw new Exception("Provider unexpectedly returned null for accepts - "
            + provider.getName());
      }
      if (!supportedContexts.isEmpty()) {
        providers.add(provider);
      }
    }
    return providers;
  }

  /**
   * Returns the process providers mapped to the output contexts supported for
   * the specified input context.
   *
   * @param inputContext the input context
   * @return the process providers
   * @throws Exception the exception
   */
  private Map<ProviderHandler, List<DataContext>> getProcessProviders(
    DataContext inputContext) throws Exception {

    final Map<ProviderHandler, List<DataContext>> providerMap = new HashMap<>();

    // Ask each provider if it accepts
    for (final ProviderHandler provider : getProviders().values()) {
      final List<DataContext> supportedContexts =
          provider.accepts(inputContext);
      if (supportedContexts == null) {
        throw new Exception("Provider unexpectedly returned null for accepts - "
            + provider.getName());
      }
      if (!supportedContexts.isEmpty()) {
        providerMap.put(provider, supportedContexts);

      }
    }
    return providerMap;
  }

  /**
   * Returns the converters.
   *
   * @param inputContext the input context
   * @param outputContext the output context
   * @return the converters
   * @throws Exception the exception
   */
  private List<ConverterHandler> getConverters(DataContext inputContext,
    DataContext outputContext) throws Exception {
    final List<ConverterHandler> converters = new ArrayList<>();

    // Ask each converter if it accepts
    for (final ConverterHandler converter : getConverters().values()) {
      final List<DataContext> supportedContexts =
          converter.accepts(inputContext);
      if (supportedContexts == null) {
        throw new Exception(
            "Converter unexpectedly returned null for accepts - "
                + converter.getName());
      }
      if (!supportedContexts.isEmpty()) {
        // See if output matches any supported Contexts
        for (final DataContext matchContext : supportedContexts) {
          if (DataContextMatcher.matches(outputContext, matchContext)) {
            converters.add(converter);
            break;
          }
        }
      }
    }
    return converters;
  }

  /* see superclass */
  @Override
  public TransformRecord addTransformRecord(TransformRecord record)
    throws Exception {
    Logger.getLogger(getClass()).debug("Add transform record - " + record);
    return addHasLastModified(record);
  }

  /* see superclass */
  @Override
  public void updateTransformRecord(TransformRecord record) throws Exception {
    Logger.getLogger(getClass()).debug("Update transform record - " + record);
    updateHasLastModified(record);
  }

  /* see superclass */
  @Override
  public void removeTransformRecord(Long recordId) throws Exception {
    Logger.getLogger(getClass()).debug("remove transform record - " + recordId);
    this.removeHasLastModified(recordId, TransformRecordJpa.class);
  }

  /* see superclass */
  @Override
  public TransformRecord getTransformRecord(Long recordId) throws Exception {
    Logger.getLogger(getClass()).debug("Get transform record - " + recordId);
    return getHasLastModified(recordId, TransformRecordJpa.class);
  }

  /* see superclass */
  @Override
  public TransformRecordList findTransformRecordsForQuery(String query,
    PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass())
        .debug("Find transform records - " + query + ", " + pfs);
    final SearchHandler searchHandler = getSearchHandler(ConfigUtility.DEFAULT);
    final int[] totalCt = new int[1];
    List<TransformRecordJpa> results = searchHandler.getQueryResults(null, null,
        Branch.ROOT, query, null, TransformRecordJpa.class,
        TransformRecordJpa.class, pfs, totalCt, getEntityManager());
    TransformRecordList list = new TransformRecordListJpa();
    list.setTotalCount(totalCt[0]);
    list.getObjects().addAll(results);
    return list;
  }

  /* see superclass */
  @Override
  public TypeKeyValue addTypeKeyValue(TypeKeyValue typeKeyValue)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Add type, key, value - " + typeKeyValue);
    return addObject(typeKeyValue);
  }

  /* see superclass */
  @Override
  public void updateTypeKeyValue(TypeKeyValue typeKeyValue) throws Exception {
    Logger.getLogger(getClass())
        .debug("Update type, key, value - " + typeKeyValue);
    updateObject(typeKeyValue);
  }

  /* see superclass */
  @Override
  public void removeTypeKeyValue(Long typeKeyValueId) throws Exception {
    Logger.getLogger(getClass())
        .debug("Remove type, key, value - " + typeKeyValueId);
    this.removeObject((TypeKeyValueJpa) getTypeKeyValue(typeKeyValueId),
        TypeKeyValueJpa.class);
  }

  /* see superclass */
  @Override
  public TypeKeyValue getTypeKeyValue(Long typeKeyValueId) throws Exception {
    Logger.getLogger(getClass())
        .debug("Get type, key, value - " + typeKeyValueId);
    return getObject(typeKeyValueId, TypeKeyValueJpa.class);
  }

  /* see superclass */
  @Override
  public List<TypeKeyValue> findTypeKeyValuesForQuery(String query)
    throws Exception {
    Logger.getLogger(getClass()).debug("Find type, key, values - " + query);
    final SearchHandler searchHandler = getSearchHandler(ConfigUtility.DEFAULT);
    final int[] totalCt = new int[1];
    return new ArrayList<TypeKeyValue>(searchHandler.getQueryResults(null, null,
        Branch.ROOT, query, null, TypeKeyValueJpa.class, TypeKeyValueJpa.class,
        null, totalCt, getEntityManager()));
  }

  /* see superclass */
  @Override
  public void closeHandlers() throws Exception {

    // Close handlers that may have resources open (e.g. for analysis)
    for (final NormalizerHandler handler : normalizers.values()) {
      handler.close();
    }

    for (final AnalyzerHandler handler : analyzers.values()) {
      handler.close();
    }

    for (final ProviderHandler handler : providers.values()) {
      handler.close();
    }

    for (final ConverterHandler handler : converters.values()) {
      handler.close();
    }

  }

}
