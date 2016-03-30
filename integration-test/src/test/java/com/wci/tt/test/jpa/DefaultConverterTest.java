/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.services.handlers.DefaultConverter;
import com.wci.tt.services.handlers.ConverterHandler;

/**
 * Some initial testing for {@link DefaultConverter}. Assumes stock dev load.
 */
public class DefaultConverterTest extends JpaSupport {

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
   * Test null inputs on accepts routine.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("unused")
  @Test
  public void testAcceptNullInputs() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();
    try {
      List<DataContext> results = handler.accepts(null);
      fail("Exception expected.");
    } catch (Exception e) {
      // n/a, expected result
    }
  }

  /**
   * Test empty input on accepts routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAcceptEmptyContent() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();
    List<DataContext> results = handler.accepts(new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test filled out input on accepts routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAcceptFullInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();

    DataContextJpa inputContext = new DataContextJpa();
    inputContext.setCustomer("Test Input Customer");
    inputContext.setInfoModelClass("test.input.class.name");
    inputContext.setSemanticType("Test Input Semantic Type");
    inputContext.setSpecialty("Test Input Specialty");
    inputContext.setTerminology("Test Input Terminology");
    inputContext.setVersion("Test Input Version");
    inputContext.setType(DataContextType.NAME);

    List<DataContext> results = handler.accepts(inputContext);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertEquals(1, results.size());

    DataContext result = results.get(0);
    assertEquals(inputContext.getType(), result.getType());
    assertFalse(inputContext.getCustomer().equals(result.getCustomer()));
    assertFalse(
        inputContext.getInfoModelClass().equals(result.getInfoModelClass()));
    assertFalse(
        inputContext.getSemanticType().equals(result.getSemanticType()));
    assertFalse(inputContext.getSpecialty().equals(result.getSpecialty()));
    assertFalse(inputContext.getTerminology().equals(result.getTerminology()));
    assertFalse(inputContext.getVersion().equals(result.getVersion()));
  }

  /**
   * Test null inputs on convert routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConvertNullInputs() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();
    DataContextTuple results = handler.convert(null, null, null, null, null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertNull(results.getData());
    assertNull(results.getDataContext());
  }

  /**
   * Test empty input on convert routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConvertEmptyContent() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();
    DataContextTuple results = handler.convert("", new DataContextJpa(),
        new DataContextJpa(), "", new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertNull(results.getData());
    assertNull(results.getDataContext());
  }

  /**
   * Test filled out input on convert routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConvertFullInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();

    String inputString = "test string";

    DataContextJpa inputContext = new DataContextJpa();
    inputContext.setCustomer("Test Input Customer");
    inputContext.setInfoModelClass("test.input.class.name");
    inputContext.setSemanticType("Test Input Semantic Type");
    inputContext.setSpecialty("Test Input Specialty");
    inputContext.setTerminology("Test Input Terminology");
    inputContext.setVersion("Test Input Version");
    inputContext.setType(DataContextType.NAME);

    DataContextJpa outputContext = new DataContextJpa();
    outputContext.setCustomer("Test Output Customer");
    outputContext.setInfoModelClass("test.output.class.name");
    outputContext.setSemanticType("Test Output Semantic Type");
    outputContext.setSpecialty("Test Output Specialty");
    outputContext.setTerminology("Test Output Terminology");
    outputContext.setVersion("Test Output Version");
    outputContext.setType(DataContextType.NAME);

    DataContextTuple results = handler.convert(inputString, inputContext,
        outputContext, inputString, inputContext);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertNotNull(results.getData());
    assertNotNull(results.getDataContext());

    assertEquals(inputString, results.getData());
    assertEquals(outputContext, results.getDataContext());
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
