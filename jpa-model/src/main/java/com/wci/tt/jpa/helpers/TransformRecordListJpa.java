/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.TransformRecordList;
import com.wci.tt.jpa.TransformRecordJpa;
import com.wci.umls.server.helpers.AbstractResultList;

/**
 * JAXB-enabled implementation of {@link TransformRecordList}.
 * 
 * Useful for sending input and output data contexts to REST Server. In such
 * case, Input is always field always field #0 and output always field #1.
 */
@XmlRootElement(name = "recordList")
public class TransformRecordListJpa extends AbstractResultList<TransformRecord>
    implements TransformRecordList {

  /* see superclass */
  @Override
  @XmlElement(type = TransformRecordJpa.class, name = "records")
  public List<TransformRecord> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "TransformRecordListJpa [records=" + getObjects() + ", getCount()="
        + getCount() + "]";
  }
}
