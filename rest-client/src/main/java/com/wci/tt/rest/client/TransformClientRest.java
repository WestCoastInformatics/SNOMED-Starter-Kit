/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredDataContextList;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.helpers.DataContextListJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextListJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleListJpa;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.KeyValuePairList;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.rest.client.RootClientRest;

/**
 * Class calling the REST Service for Transform routines for
 * {@link TransformServiceRest}.
 */
public class TransformClientRest extends RootClientRest
    implements TransformServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link TransformClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public TransformClientRest(Properties config) {
    this.config = config;
  }

  /* see superclass */
  @Override
  public ScoredDataContextList identify(String inputStr,
    DataContextJpa dataContext, String authToken) throws Exception {
    Logger.getLogger(getClass())
        .debug("Transform Client - identify " + inputStr + ", " + dataContext);

    validateNotEmpty(inputStr, "inputStr");

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(
        config.getProperty("base.url") + "/transform/identify/" + inputStr);

    String contextString = ConfigUtility.getStringForGraph(
        dataContext == null ? new DataContextJpa() : dataContext);
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).post(Entity.xml(contextString));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ScoredDataContextListJpa result = (ScoredDataContextListJpa) ConfigUtility
        .getGraphForString(resultString, ScoredDataContextListJpa.class);

    return result;
  }

  /* see superclass */
  @Override
  public ScoredDataContextTupleList process(String inputStr,
    DataContextListJpa inputOutputContexts, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Transform Client - identify " + inputStr + ", " + inputOutputContexts);

    validateNotEmpty(inputStr, "inputStr");

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(
        config.getProperty("base.url") + "/transform/process/" + inputStr);

    // Ensure the JPA class has content before passing to server
    List<DataContext> updatedInputOutputContexts = new ArrayList<>();

    if (inputOutputContexts == null) {
      inputOutputContexts = new DataContextListJpa();
    }

    if (inputOutputContexts.getObjects().get(0) == null) {
      updatedInputOutputContexts.add(new DataContextJpa());
    } else {
      updatedInputOutputContexts.add(inputOutputContexts.getObjects().get(0));
    }

    if (inputOutputContexts.getObjects().get(1) == null) {
      updatedInputOutputContexts.add(new DataContextJpa());
    } else {
      updatedInputOutputContexts.add(inputOutputContexts.getObjects().get(1));
    }

    inputOutputContexts.setObjects(updatedInputOutputContexts);
    inputOutputContexts.setTotalCount(2);

    // JPA Object ready for conversion
    String inputOutputContextString =
        ConfigUtility.getStringForGraph(inputOutputContexts);

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken)
        .post(Entity.xml(inputOutputContextString));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ScoredDataContextTupleListJpa result =
        (ScoredDataContextTupleListJpa) ConfigUtility.getGraphForString(
            resultString, ScoredDataContextTupleListJpa.class);

    return result;
  }

  @Override
  public StringList getSpecialties(String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("Transform Client - specialties");

    Client client = ClientBuilder.newClient();
    WebTarget target = client
        .target(config.getProperty("base.url") + "/transform/specialties");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, StringList.class);

  }

  @Override
  public StringList getSemanticTypes(String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("Transform Client - stys");

    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(config.getProperty("base.url") + "/transform/stys");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, StringList.class);

  }

  @Override
  public KeyValuePairList getSourceDataLoaders(String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug("Transform Client - loaders ");

    Client client = ClientBuilder.newClient();
    WebTarget target = client
        .target(config.getProperty("base.url") + "/transform/data/loaders");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        KeyValuePairList.class);
  }

  @Override
  public KeyValuePairList getNormalizers(String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("Transform Client - normalizers ");

    Client client = ClientBuilder.newClient();
    WebTarget target = client
        .target(config.getProperty("base.url") + "/transform/normalizers");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();
    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        KeyValuePairList.class);
  }

  @Override
  public KeyValuePairList getProviders(String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("Transform Client - providers");

    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(config.getProperty("base.url") + "/transform/providers");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();
    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        KeyValuePairList.class);
  }

  @Override
  public KeyValuePairList getConverters(String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("Transform Client - converters");

    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(config.getProperty("base.url") + "/transform/converters");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();
    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        KeyValuePairList.class);
  }

}
