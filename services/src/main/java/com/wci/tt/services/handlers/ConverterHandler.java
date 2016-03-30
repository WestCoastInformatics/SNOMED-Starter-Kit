/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.umls.server.helpers.Configurable;

/**
 * Interface responsible for taking the input string defined by its input
 * context and converting it to output in the form defined by the output
 * context.
 * 
 * Results returned are ranked by probability per provider.
 */
public interface ConverterHandler extends Configurable {

  /**
   * Ensures input context is supported. If it is, returns all output contexts
   * the converter method supports. Returns an empty list if the input context
   * is not supported.
   * 
   * Returns a list because converter may handle multiple output data contexts.
   * 
   * Note: Often overridden by AbstractAcceptsHandler.
   *
   * @param context the context
   * @return the list
   * @throws Exception the exception
   */
  public List<DataContext> accepts(DataContext context) throws Exception;

  /**
   * Changes the representation of the inputStr defined by the specified input
   * context into output in the form as defined by the output context.
   * 
   * Four convert possibilities exist:
   * 
   * <pre>
   *    1) A code to an Information Model 
   *    2) A code to a code 
   *    3) A Information Model to a code 
   *    4) A Information Model to a Information Model
   * </pre>
   *
   * @param inputStr the input string
   * @param inputContext the input context
   * @param outputContext the output context
   * @param origInputString the orig input string
   * @param origInputContext the orig input context
   * @return the data context tuple
   * @throws Exception the exception if the input/output context combination is
   *           not supported
   */
  public DataContextTuple convert(String inputStr, DataContext inputContext,
    DataContext outputContext, String origInputString,
    DataContext origInputContext) throws Exception;

  /**
   * Close any open resources on application shutdown.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;

}
