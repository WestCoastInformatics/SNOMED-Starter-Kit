/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.algo;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.services.SourceDataService;
import com.wci.umls.server.jpa.algo.RemoveTerminologyAlgorithm;

/**
 * Implementation of an algorithm to remove a terminology and the associated
 * {@link SourceData} and {@link SourceDataFile}s.
 * 
 */
public class RemoveSourceDataAlgorithm extends RemoveTerminologyAlgorithm {

  /** The source data. */
  private SourceData sourceData;

  /**
   * Instantiates an empty {@link RemoveSourceDataAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public RemoveSourceDataAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the source data.
   *
   * @param sourceData the source data
   */
  public void setSourceData(SourceData sourceData) {
    this.sourceData = sourceData;
  }

  /* see superclass */
  @Override
  public void compute() throws Exception {

    // Remove the terminology
    super.compute();

    if (sourceData != null) {
      // Remove the source data
      final SourceDataService service = new SourceDataServiceJpa();
      try {
        service.removeSourceData(sourceData.getId());

        for (final SourceDataFile file : sourceData.getSourceDataFiles()) {
          service.removeSourceDataFile(file.getId());
        }

      } catch (Exception e) {
        throw e;
      } finally {
        service.close();
      }
    }

  }

}
