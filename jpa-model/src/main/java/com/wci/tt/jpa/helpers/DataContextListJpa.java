/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextList;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.umls.server.helpers.AbstractResultList;

/**
 * JAXB-enabled implementation of {@link DataContextList}.
 * 
 * Useful for sending input and output data contexts to REST Server. In such
 * case, Input is always field always field #0 and output always field #1.
 */
@XmlRootElement(name = "dataContextList")
public class DataContextListJpa extends AbstractResultList<DataContext>
    implements DataContextList {

  /* see superclass */
  @Override
  @XmlElement(type = DataContextJpa.class, name = "dataContexts")
  public List<DataContext> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "DataContextListJpa [DataContexts=" + getObjects() + ", getCount()="
        + getCount() + "]";
  }
}
