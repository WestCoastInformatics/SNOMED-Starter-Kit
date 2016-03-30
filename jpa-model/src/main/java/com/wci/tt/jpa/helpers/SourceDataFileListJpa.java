/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.SourceDataFile;
import com.wci.tt.helpers.SourceDataFileList;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.umls.server.helpers.AbstractResultList;

/**
 * JAXB enabled implementation of {@link SourceDataFileList}.
 */
@XmlRootElement(name = "sourceDataFileList")
public class SourceDataFileListJpa extends AbstractResultList<SourceDataFile>
    implements SourceDataFileList {

  /* see superclass */
  @Override
  @XmlElement(type = SourceDataFileJpa.class, name = "sourceDataFiles")
  public List<SourceDataFile> getObjects() {
    return super.getObjectsTransient();
  }

}
