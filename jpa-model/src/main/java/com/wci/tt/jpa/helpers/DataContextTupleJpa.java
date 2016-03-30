/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.jpa.DataContextJpa;

/**
 * JPA enabled implementation of {@link DataContextTuple}.
 */
@XmlRootElement(name = "dataContextTuple")
public class DataContextTupleJpa implements DataContextTuple {

  /** The id. */
  private Long id;

  /** The data being analyzed. */
  private String data;

  /** The context associated with the data. */
  private DataContext context;

  /**
   * 
   * Instantiates an empty {@link DataContextTupleJpa}.
   */
  public DataContextTupleJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link DataContextTupleJpa} from the specified parameters.
   *
   * @param tuple the data context tuple
   */
  public DataContextTupleJpa(DataContextTuple tuple) {
    this.id = tuple.getId();
    this.data = tuple.getData();
    this.context = tuple.getDataContext();
  }

  /* see superclass */
  @Override
  public Long getId() {
    return id;
  }

  /* see superclass */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /* see superclass */
  @Override
  public String getData() {
    return data;
  }

  /* see superclass */
  @Override
  public void setData(String data) {
    this.data = data;
  }

  /* see superclass */
  @XmlElement(type = DataContextJpa.class, name = "dataContext")
  @Override
  public DataContext getDataContext() {
    return context;
  }

  /* see superclass */
  @Override
  public void setDataContext(DataContext context) {
    this.context = context;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    result = prime * result + ((context == null) ? 0 : context.hashCode());
    return result;
  }

  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DataContextTupleJpa other = (DataContextTupleJpa) obj;
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (!data.equals(other.data))
      return false;
    if (context == null) {
      if (other.context != null)
        return false;
    } else if (!context.equals(other.context))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "DataContextTupleJpa [id=" + id + ", data=" + data + ", context="
        + context + "]";
  }
}
