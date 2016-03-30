/*

 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.tt.helpers.ScoredDataContextList;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.helpers.DataContextListJpa;
import com.wci.umls.server.helpers.KeyValuePairList;
import com.wci.umls.server.helpers.StringList;

/**
 * Lists a transform routines available via a REST service.
 */
public interface TransformServiceRest {

  /**
   * Identify all Providers supported data contexts.
   *
   * @param inputStr the input string
   * @param dataContext the data context
   * @param authToken the authentication token
   * @return the scored data context list
   * @throws Exception the exception
   */
  public ScoredDataContextList identify(String inputStr,
    DataContextJpa dataContext, String authToken) throws Exception;

  /**
   * Process and Convert on all supported input/output data contexts.
   *
   * @param inputStr the input string
   * @param inputOutputContexts the input output contexts
   * @param authToken the authentication token
   * @return the scored data context tuple list
   * @throws Exception the exception
   */
  public ScoredDataContextTupleList process(String inputStr,
    DataContextListJpa inputOutputContexts, String authToken) throws Exception;

  /**
   * Returns the specialties.
   *
   * @param authToken the auth token
   * @return the specialties
   * @throws Exception the exception
   */
  public StringList getSpecialties(String authToken) throws Exception;

  /**
   * Returns the semantic types.
   *
   * @param authToken the auth token
   * @return the semantic types
   * @throws Exception the exception
   */
  public StringList getSemanticTypes(String authToken) throws Exception;

  /**
   * Returns the source data loaders.
   *
   * @param authToken the auth token
   * @return the source data loaders
   * @throws Exception the exception
   */
  public KeyValuePairList getSourceDataLoaders(String authToken)
    throws Exception;

  /**
   * Returns the normalizers.
   *
   * @param authToken the auth token
   * @return the normalizers
   * @throws Exception the exception
   */
  public KeyValuePairList getNormalizers(String authToken) throws Exception;

  /**
   * Returns the providers.
   *
   * @param authToken the auth token
   * @return the providers
   * @throws Exception the exception
   */
  public KeyValuePairList getProviders(String authToken) throws Exception;

  /**
   * Returns the converters.
   *
   * @param authToken the auth token
   * @return the converters
   * @throws Exception the exception
   */
  public KeyValuePairList getConverters(String authToken) throws Exception;
}