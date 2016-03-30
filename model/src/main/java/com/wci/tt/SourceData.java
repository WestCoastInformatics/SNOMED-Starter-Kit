/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt;

import java.util.List;

import com.wci.umls.server.helpers.HasId;
import com.wci.umls.server.helpers.HasLastModified;
import com.wci.umls.server.helpers.HasName;

/**
 * Generically represents a collection of source data files and a loader used
 * for those files.
 */
public interface SourceData extends HasId, HasLastModified, HasName {

  /**
   * Load status for a {@link SourceData}.
   */
  public enum Status {
    /** The unknown. */
    UNKNOWN,
    /** The loading. */
    LOADING,
    /** The finished. */
    FINISHED,
    /** The failed. */
    FAILED
  }

  /**
   * Sets the source data files.
   *
   * @param sourceDataFiles the source data files
   */
  public void setSourceDataFiles(List<SourceDataFile> sourceDataFiles);

  /**
   * Gets the source data files.
   *
   * @return the source data files
   */
  public List<SourceDataFile> getSourceDataFiles();

  /**
   * Sets the config file key for the loader.
   *
   * @param loader the loader
   */
  public void setLoader(String loader);

  /**
   * Gets the config file key for the loader.
   *
   * @return the loader
   */
  public String getLoader();

  /**
   * Returns the loader status.
   *
   * @return the loader status
   */
  public SourceData.Status getLoaderStatus();

  /**
   * Sets the loader status.
   *
   * @param loaderStatus the loader status
   */
  public void setLoaderStatus(SourceData.Status loaderStatus);

  /**
   * Returns the description.
   *
   * @return the description
   */
  /* see superclass */
  public String getDescription();

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description);

}
