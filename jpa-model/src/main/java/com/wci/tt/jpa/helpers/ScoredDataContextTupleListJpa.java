package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.umls.server.helpers.AbstractResultList;

/**
 * JAXB-enabled implementation of {@link ScoredDataContextTupleList}.
 */
@XmlRootElement(name = "scoredDataContextTupleList")
public class ScoredDataContextTupleListJpa
    extends AbstractResultList<ScoredDataContextTuple>
    implements ScoredDataContextTupleList {

  /* see superclass */
  @Override
  @XmlElement(type = ScoredDataContextTupleJpa.class, name = "scoredDataContextTuples")
  public List<ScoredDataContextTuple> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredDataContextTupleListJpa [scoredDataContextTuple="
        + getObjects() + ", getCount()=" + getCount() + "]";
  }
}
