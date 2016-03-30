/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.infomodels;

import com.wci.umls.server.helpers.Configurable;

/**
 * Generically represents an information model.
 *
 * @param <T> the
 */
public interface InfoModel<T extends InfoModel<?>> extends Configurable {

  /** The Constant MULTIPLE_VALUES. */
  public final static String MULTIPLE_VALUES = "*";

  /**
   * Verifies that the model expressed in the parameter is an example of this
   * type of model.
   *
   * @param model the model
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean verify(String model) throws Exception;

  /**
   * Returns an object representing the model.
   *
   * @param model the model
   * @return the model
   * @throws Exception the exception
   */
  public T getModel(String model) throws Exception;

  /**
   * Returns the model value, the JSON or XML string representing the model.
   *
   * @return the model value
   * @throws Exception the exception
   */
  public String getModelValue() throws Exception;

  /**
   * Returns the model in common.
   *
   * @param model the model
   * @param analysisMode the analysis mode flag
   * @return the model in common
   * @throws Exception the exception
   */
  public T getModelInCommon(T model, boolean analysisMode) throws Exception;

  /**
   * Returns the version of the model.
   *
   * @return the version
   */
  public String getVersion();

  /**
   * Algorithm to identify if the model in question has a score within a given
   * range of the model with the maximum score.
   *
   * @param score the score
   * @param maxScore the max score
   * @param minCommonScore the min common score
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public static boolean isCommonModel(float score, float maxScore,
    float minCommonScore) {
    return (maxScore * minCommonScore) + score >= maxScore;
  }

  /**
   * Indicates whether or not the score of a model is acceptable.
   *
   * @param score the score
   * @param floorModelScore the floor model score
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public static boolean isValidValue(float score, float floorModelScore) {
    return score >= floorModelScore;
  }

}
