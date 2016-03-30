package com.wci.tt;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;

/**
 * The Interface IdentifyHandler.
 */
public interface IdentifyHandler {

  /**
   * Identify.
   *
   * @param string the string
   * @param dataContext the data context
   * @return the quality result list
   * @throws Exception the exception
   */
  ScoredResultList identify(String string, DataContext dataContext)
    throws Exception;

  /**
   * Identify.
   *
   * @param qr the qr
   * @param dataContext the data context
   * @return the quality result list
   * @throws Exception the exception
   */
  ScoredResultList identify(ScoredResult qr, DataContext dataContext)
    throws Exception;
}
