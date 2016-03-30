/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.infomodels.InfoModel;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.services.CoordinatorService;

/**
 * Some initial testing for {@link CoordinatorServiceJpa}. Assumes stock dev
 * load.
 */
public class CoordinatorServiceTest extends JpaSupport {

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    // do nothing
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    // n/a
  }

  /**
   * Test that information models are loaded as expected.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAcccessInformationModels() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    CoordinatorService service = new CoordinatorServiceJpa();
    Map<String, InfoModel<?>> models = service.getInformationModels();
    assertEquals(1, models.size());
  }

  /**
   * Test cases where call to identify expected not to return results.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAllIdentifyCallNoResultAnalysis() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    CoordinatorService service = new CoordinatorServiceJpa();

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

    // Null Inputs
    List<ScoredDataContext> results = service.identify(null, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Inputs
    results = service.identify("", new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Data with Null Context
    results = service.identify("", null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Data with Empty Context
    results = service.identify(null, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Data with Empty Context
    results = service.identify(null, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Data with Filled Context
    results = service.identify("", inputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Data with Filled Context
    results = service.identify(null, inputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Filled Data with Null Context
    results = service.identify(inputString, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());

    // Filled Data with Empty Context
    results = service.identify(inputString, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());

    // Filled Inputs
    results = service.identify(inputString, inputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
  }

  /**
   * Test cases where call to process expected not to return results.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAllProcessCallsNoResultAnalysis() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    CoordinatorService service = new CoordinatorServiceJpa();

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

    // PART 1: Null Input String (9 combinations)
    // Null Contexts
    List<ScoredResult> results = service.process(null, null, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Input Context and Empty Output Context
    results = service.process(null, null, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Input Context and Valid Output Context
    results = service.process(null, null, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Input Context and Null Output Context
    results = service.process(null, new DataContextJpa(), null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Contexts
    results = service.process(null, new DataContextJpa(), new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Input Context and Valid Output Context
    results = service.process(null, new DataContextJpa(), outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Input Context and Null Output Context
    results = service.process(null, inputContext, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Input Context and Empty Output Context
    results = service.process(null, inputContext, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Contexts
    results = service.process(null, inputContext, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // PART 2: Empty Input String
    // Null Contexts
    results = service.process("", null, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Input Context and Empty Output Context
    results = service.process("", null, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Input Context and Valid Output Context
    results = service.process("", null, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Input Context and Null Output Context
    results = service.process("", new DataContextJpa(), null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Contexts
    results = service.process("", new DataContextJpa(), new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Input Context and Valid Output Context
    results = service.process("", new DataContextJpa(), outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Input Context and Null Output Context
    results = service.process("", inputContext, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Input Context and Empty Output Context
    results = service.process("", inputContext, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Contexts
    results = service.process("", inputContext, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // PART 3: Valid Input String
    // Null Contexts
    results = service.process(inputString, null, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Input Context and Empty Output Context
    results = service.process(inputString, null, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Null Input Context and Valid Output Context
    results = service.process(inputString, null, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());

    // Empty Input Context and Null Output Context
    results = service.process(inputString, new DataContextJpa(), null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Contexts
    results = service.process(inputString, new DataContextJpa(),
        new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Empty Input Context and Valid Output Context
    results = service.process(inputString, new DataContextJpa(), outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());

    // Valid Input Context and Null Output Context
    results = service.process(inputString, inputContext, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Input Context and Empty Output Context
    results = service.process(inputString, inputContext, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(0, results.size());

    // Valid Contexts
    results = service.process(inputString, inputContext, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
  }

  /**
   * Test cases where call to identify expected to return proper results.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAllIdentifyCallAnalyzeResults() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    CoordinatorService service = new CoordinatorServiceJpa();

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

    // Filled Data with Null Context
    List<ScoredDataContext> results = service.identify(inputString, null);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
    ScoredDataContext result = results.get(0);
    assertEquals(null, result.getCustomer());
    assertEquals(null, result.getInfoModelClass());
    assertEquals(null, result.getSemanticType());
    assertEquals(null, result.getSpecialty());
    assertEquals(null, result.getTerminology());
    assertEquals(null, result.getVersion());
    assertEquals(DataContextType.UNKNOWN, result.getType());
    assertTrue(result.getScore() == 1f);

    // Filled Data with Empty Context
    results = service.identify(inputString, new DataContextJpa());
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
    result = results.get(0);
    assertEquals(null, result.getCustomer());
    assertEquals(null, result.getInfoModelClass());
    assertEquals(null, result.getSemanticType());
    assertEquals(null, result.getSpecialty());
    assertEquals(null, result.getTerminology());
    assertEquals(null, result.getVersion());
    assertEquals(DataContextType.UNKNOWN, result.getType());
    assertTrue(result.getScore() == 1f);

    // Filled Inputs
    results = service.identify(inputString, inputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
    result = results.get(0);
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

    CoordinatorService service = new CoordinatorServiceJpa();

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

    // Null Input Context and Valid Output Context
    List<ScoredResult> results =
        service.process(inputString, null, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
    ScoredResult result = results.get(0);
    assertEquals(inputString, result.getValue());
    assertTrue(result.getScore() == 1f);

    // Empty Input Context and Valid Output Context
    results = service.process(inputString, new DataContextJpa(), outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
    result = results.get(0);
    assertEquals(inputString, result.getValue());
    assertTrue(result.getScore() == 1f);

    // Valid Contexts
    results = service.process(inputString, inputContext, outputContext);
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(1, results.size());
    result = results.get(0);
    assertEquals(inputString, result.getValue());
    assertTrue(result.getScore() == 1f);
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {
    // n/a
  }

  /**
   * Teardown class.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void teardownClass() throws Exception {
    // do nothing
  }
}
