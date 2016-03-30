/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;

/**
 * The Interface ProcessHandler.
 */
public interface ProcessHandler {

  /**
   * Process.
   *
   * @param qr the qr
   * @param dataContext the data context
   * @return the scored result list
   */
  public ScoredResultList process(ScoredResult qr, DataContext dataContext);
}
