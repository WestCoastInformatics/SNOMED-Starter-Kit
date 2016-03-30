/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.DataContext;
import com.wci.umls.server.helpers.HasId;

/**
 * Interface representing data along with its associated DataContext
 */
public interface DataContextTuple extends HasId {

  /**
   * Returns the data.
   *
   * @return the data
   */
  public String getData();

  /**
   * Sets the data.
   *
   * @param data the data to set
   */
  public void setData(String data);

  /**
   * Returns the data context.
   *
   * @return the data context
   */
  public DataContext getDataContext();

  /**
   * Sets the data context.
   *
   * @param context the data context
   */
  public void setDataContext(DataContext context);
}
