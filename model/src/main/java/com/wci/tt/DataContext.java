/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt;

import com.wci.tt.helpers.DataContextType;
import com.wci.umls.server.helpers.HasId;
import com.wci.umls.server.helpers.HasTerminology;

/**
 * Interface representing the context known about the data being processed.
 */
public interface DataContext extends HasTerminology, HasId {

  /**
   * Returns the data context type.
   *
   * @return the data context type
   */
  public DataContextType getType();

  /**
   * Sets the data context type.
   *
   * @param type the data type
   */
  public void setType(DataContextType type);

  /**
   * Returns the customer.
   *
   * @return the customer
   */
  public String getCustomer();

  /**
   * Sets the customer.
   *
   * @param customer the customer
   */
  public void setCustomer(String customer);

  /**
   * Returns the semantic type.
   *
   * @return the semantic type
   */
  public String getSemanticType();

  /**
   * Sets the semantic type.
   *
   * @param semanticType the semantic type
   */
  public void setSemanticType(String semanticType);

  /**
   * Returns the specialty.
   *
   * @return the specialty
   */
  public String getSpecialty();

  /**
   * Sets the specialty.
   *
   * @param specialty the specialty
   */
  public void setSpecialty(String specialty);

  /**
   * Returns the info model class name.
   *
   * @return the info model class name
   */
  public String getInfoModelClass();

  /**
   * Sets the info model class name.
   *
   * @param className the info model class
   */
  public void setInfoModelClass(String className);

  /**
   * Investigates whether at least one aspect of dataContext defined.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isEmpty();

}
