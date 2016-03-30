/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.services.handlers.DefaultNormalizer;
import com.wci.tt.services.handlers.NormalizerHandler;

/**
 * Some initial testing for {@link DefaultNormalizer}. Assumes stock dev
 * load.
 */
public class DefaultNormalizerTest extends JpaSupport {

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
   * Test null inputs on normalize routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalizeNullInputs() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    NormalizerHandler handler = new DefaultNormalizer();
    List<ScoredResult> results = handler.normalize(null, null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test empty input on normalize routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalizeEmptyContent() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    NormalizerHandler handler = new DefaultNormalizer();
    List<ScoredResult> results = handler.normalize("", new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test filled out input on normalize routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalizeFullInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    NormalizerHandler handler = new DefaultNormalizer();

    String inputString = "test string";

    DataContextJpa context = new DataContextJpa();
    context.setCustomer("Test Customer");
    context.setInfoModelClass("test.model.class.name");
    context.setSemanticType("Test Semantic Type");
    context.setSpecialty("Test Specialty");
    context.setTerminology("Test Terminology");
    context.setVersion("Test Version");

    // Test works for TEXT DataContextType 
    context.setType(DataContextType.CODE);
    List<ScoredResult> results = handler.normalize(inputString, context);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(1, results.size());
    ScoredResult result = results.get(0);

    assertTrue(result.getScore() == 1f);
    assertEquals(inputString, result.getValue());
    assertFalse(result.isObsolete());
    
    // Test works for TEXT DataContextType 
    context.setType(DataContextType.TEXT);
    results = handler.normalize(inputString, context);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(1, results.size());
    result = results.get(0);

    assertTrue(result.getScore() == 1f);
    assertEquals(inputString, result.getValue());
    assertFalse(result.isObsolete());

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
