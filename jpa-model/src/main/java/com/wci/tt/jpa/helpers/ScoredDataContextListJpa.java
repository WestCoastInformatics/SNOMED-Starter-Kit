/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredDataContextList;
import com.wci.umls.server.helpers.AbstractResultList;

/**
 * JAXB-enabled implementation of {@link ScoredDataContextList}.
 */
@XmlRootElement(name = "scoredDataContextList")
public class ScoredDataContextListJpa extends
    AbstractResultList<ScoredDataContext> implements ScoredDataContextList {

  /* see superclass */
  @Override
  @XmlElement(type = ScoredDataContextJpa.class, name = "scoredDataContexts")
  public List<ScoredDataContext> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredDataContextListJpa [scoredDataContexts=" + getObjects()
        + ", getCount()=" + getCount() + "]";
  }
}
