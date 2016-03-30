/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;
import com.wci.umls.server.helpers.AbstractResultList;

/**
 * JAXB-enabled implementation of {@link ScoredResultList}.
 */
@XmlRootElement(name = "qualityResultList")
public class ScoredResultListJpa extends AbstractResultList<ScoredResult>
    implements ScoredResultList {

  /* see superclass */
  @Override
  @XmlElement(type = ScoredResultJpa.class, name = "results")
  public List<ScoredResult> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "QualityResultListJpa [results=" + getObjects() + ", getCount()="
        + getCount() + "]";
  }

}
