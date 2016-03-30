/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.SourceData;
import com.wci.tt.helpers.SourceDataList;
import com.wci.tt.jpa.SourceDataJpa;
import com.wci.umls.server.helpers.AbstractResultList;

/**
 * JAXB-enabled implementation of {@link SourceDataList}.
 */
@XmlRootElement(name = "sourceDataList")
public class SourceDataListJpa extends AbstractResultList<SourceData>
    implements SourceDataList {

  /* see superclass */
  @Override
  @XmlElement(type = SourceDataJpa.class, name = "sourceDatas")
  public List<SourceData> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "SourceDataListJpa [sourceDatas=" + getObjects() + ", getCount()="
        + getCount() + "]";
  }

}
