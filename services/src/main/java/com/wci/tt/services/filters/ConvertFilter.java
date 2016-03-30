/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.filters;

import com.wci.tt.infomodels.InfoModel;

/**
 * The Interface ConvertFilter.
 */
public interface ConvertFilter {

  /**
   * Goes through all convert filters to determine if model instance compiles
   * with the Model's rules based on term and score.
   *
   * @param inputStr the input str
   * @param infoModel the model
   * @param score the score
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  boolean isValidModel(String inputStr, InfoModel<?> infoModel, float score)
    throws Exception;

}
