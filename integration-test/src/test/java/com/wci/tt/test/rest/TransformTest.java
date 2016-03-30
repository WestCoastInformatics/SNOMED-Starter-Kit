/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredDataContextList;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.helpers.DataContextListJpa;
import com.wci.tt.rest.client.TransformClientRest;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.rest.client.SecurityClientRest;

/**
 * Some initial testing for {@link TransformClientRest}. Assumes stock dev load.
 */
public class TransformTest extends RestIntegrationSupport {

  /** The adminAuthToken auth token. */
  protected static String adminAuthToken;

  /** The transform transformService. */
  protected static TransformClientRest transformService;

  /** The security transformService. */
  protected static SecurityClientRest securityService;

  /** The properties. */
  protected static Properties properties;

  /** The test adminAuthToken username. */
  protected static String adminUser;

  /** The test adminAuthToken password. */
  protected static String adminPassword;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    // instantiate properties
    properties = ConfigUtility.getConfigProperties();

    // instantiate required services
    transformService = new TransformClientRest(properties);
    securityService = new SecurityClientRest(properties);

    // test run.config.ts has adminAuthToken user
    adminUser = properties.getProperty("admin.user");
    adminPassword = properties.getProperty("admin.password");

    if (adminUser == null || adminUser.isEmpty()) {
      throw new Exception(
          "Test prerequisite: adminAuthToken.user must be specified");
    }
    if (adminPassword == null || adminPassword.isEmpty()) {
      throw new Exception(
          "Test prerequisite: adminAuthToken.password must be specified");
    }
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {

    // authentication
    adminAuthToken =
        securityService.authenticate(adminUser, adminPassword).getAuthToken();
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @After
  public void teardown() throws Exception {
    // logout
    securityService.logout(adminAuthToken);
  }

  /**
   * Test cases where call to identify expected to return proper results.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNonexistentTrackingRecordsAccess() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String inputString = "test string";

    DataContextJpa inputContext = new DataContextJpa();
    inputContext.setCustomer("Test Input Customer");
    inputContext.setInfoModelClass("test.input.class.name");
    inputContext.setSemanticType("Test Input Semantic Type");
    inputContext.setSpecialty("Test Input Specialty");
    inputContext.setTerminology("Test Input Terminology");
    inputContext.setVersion("Test Input Version");
    inputContext.setType(DataContextType.CODE);

    // TEST 1: Filled Data with Null Context
    ScoredDataContextList results =
        transformService.identify(inputString, null, adminAuthToken);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.getCount());
    ScoredDataContext result = results.getObjects().get(0);
    assertEquals(null, result.getCustomer());
    assertEquals(null, result.getInfoModelClass());
    assertEquals(null, result.getSemanticType());
    assertEquals(null, result.getSpecialty());
    assertEquals(null, result.getTerminology());
    assertEquals(null, result.getVersion());
    assertEquals(DataContextType.UNKNOWN, result.getType());
    assertTrue(result.getScore() == 1f);

    // TEST 2: Filled Data with Empty Context
    results =
        transformService.identify(inputString, new DataContextJpa(),
            adminAuthToken);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.getCount());
    result = results.getObjects().get(0);
    assertEquals(null, result.getCustomer());
    assertEquals(null, result.getInfoModelClass());
    assertEquals(null, result.getSemanticType());
    assertEquals(null, result.getSpecialty());
    assertEquals(null, result.getTerminology());
    assertEquals(null, result.getVersion());
    assertEquals(DataContextType.UNKNOWN, result.getType());
    assertTrue(result.getScore() == 1f);

    // TEST 3: Filled Inputs
    results =
        transformService.identify(inputString, inputContext, adminAuthToken);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.getCount());
    result = results.getObjects().get(0);
    assertEquals(inputContext.getCustomer(), result.getCustomer());
    assertEquals(inputContext.getInfoModelClass(), result.getInfoModelClass());
    assertEquals(inputContext.getSemanticType(), result.getSemanticType());
    assertEquals(inputContext.getSpecialty(), result.getSpecialty());
    assertEquals(inputContext.getTerminology(), result.getTerminology());
    assertEquals(inputContext.getVersion(), result.getVersion());
    assertEquals(inputContext.getType(), result.getType());
    assertTrue(result.getScore() == 1f);
  }

  /**
   * Test cases where call to process expected to return proper results.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAllProcessCallsAnalyzeResults() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String inputString = "test string";

    DataContextJpa inputContext = new DataContextJpa();
    inputContext.setCustomer("Test Input Customer");
    inputContext.setInfoModelClass("test.input.class.name");
    inputContext.setSemanticType("Test Input Semantic Type");
    inputContext.setSpecialty("Test Input Specialty");
    inputContext.setTerminology("Test Input Terminology");
    inputContext.setVersion("Test Input Version");
    inputContext.setType(DataContextType.CODE);

    DataContextJpa outputContext = new DataContextJpa();
    outputContext.setCustomer("Test Output Customer");
    outputContext.setInfoModelClass("test.output.class.name");
    outputContext.setSemanticType("Test Output Semantic Type");
    outputContext.setSpecialty("Test Output Specialty");
    outputContext.setTerminology("Test Output Terminology");
    outputContext.setVersion("Test Output Version");
    outputContext.setType(DataContextType.CODE);

    // TEST 1: Null Input Context and Valid Output Context
    DataContextListJpa inputOutputContextsList =
        createContextObject(null, outputContext);

    ScoredDataContextTupleList results =
        transformService.process(inputString, inputOutputContextsList,
            adminAuthToken);

    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.getCount());
    ScoredDataContextTuple result = results.getObjects().get(0);

    assertEquals(inputString, result.getData());
    assertEquals(outputContext, result.getDataContext());
    assertTrue(result.getScore() == 1f);

    // TEST 2: Empty Input Context and Valid Output Context
    inputOutputContextsList =
        createContextObject(new DataContextJpa(), outputContext);

    results =
        transformService.process(inputString, inputOutputContextsList,
            adminAuthToken);

    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.getCount());

    result = results.getObjects().get(0);
    assertEquals(inputString, result.getData());
    assertEquals(outputContext, result.getDataContext());
    assertTrue(result.getScore() == 1f);

    // TEST 3: Valid Contexts
    inputOutputContextsList = createContextObject(inputContext, outputContext);

    results =
        transformService.process(inputString, inputOutputContextsList,
            adminAuthToken);

    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.getCount());

    result = results.getObjects().get(0);
    assertEquals(inputString, result.getData());
    assertEquals(outputContext, result.getDataContext());
    assertTrue(result.getScore() == 1f);
  }

  /**
   * Creates the context object.
   *
   * @param object the object
   * @param outputContext the output context
   * @return the data context list jpa
   */
  private DataContextListJpa createContextObject(Object object,
    DataContextJpa outputContext) {
    DataContextListJpa inputOutputContextsList = new DataContextListJpa();
    List<DataContext> contexts = new ArrayList<>();

    contexts.add(null);
    contexts.add(outputContext);
    inputOutputContextsList.setObjects(contexts);

    return inputOutputContextsList;
  }

}
