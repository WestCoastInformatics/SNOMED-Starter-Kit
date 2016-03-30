/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.infomodels.InfoModel;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * Default implementation of an information model. NOTE: the information model
 * type parameter must match the class.
 */
@XmlRootElement(name = "codeString")
public class DefaultInfoModel implements InfoModel<DefaultInfoModel> {

  /** The code. */
  private String code;

  /** The term. */
  private String term;

  /**
   * Instantiates an empty {@link DefaultInfoModel}.
   */
  public DefaultInfoModel() {
    // n/a
  }

  /**
   * Instantiates a {@link DefaultInfoModel} from the specified parameters.
   *
   * @param model the model
   */
  public DefaultInfoModel(DefaultInfoModel model) {
    term = model.getTerm();
    code = model.getCode();
  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  /* see superclass */
  @Override
  public String getName() {
    return "Default Information Model";
  }

  /**
   * Sets the properties.
   *
   * @param p the properties
   * @throws Exception the exception
   */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a - no configuration
  }

  /**
   * Returns the code.
   *
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * Sets the code.
   *
   * @param code the code
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Returns the term.
   *
   * @return the term
   */
  public String getTerm() {
    return term;
  }

  /**
   * Sets the term.
   *
   * @param term the term
   */
  public void setTerm(String term) {
    this.term = term;
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, DefaultInfoModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  public DefaultInfoModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, DefaultInfoModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
    }
  }

  /* see superclass */
  @Override
  public String getVersion() {
    return "1.0";
  }

  /* see superclass */
  @Override
  public String getModelValue() throws Exception {
    return "{ code : \"" + code + "\", term : \"" + term + "\"}";
  }

  @Override
  public DefaultInfoModel getModelInCommon(DefaultInfoModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    DefaultInfoModel common = new DefaultInfoModel();
    if (model.getCode() != null && model.getCode().equals(code)) {
      common.setCode(code);
    }
    if (model.getTerm() != null && model.getTerm().equals(term)) {
      common.setTerm(term);
    }
    return common;
  }

}
