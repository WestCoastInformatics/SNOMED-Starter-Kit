/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.code;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.rest.client.TransformClientRest;
import com.wci.tt.test.rest.RestIntegrationSupport;

/**
 * Some initial testing for {@link TransformClientRest}. Assumes stock dev load.
 */
public class RestApiTest extends RestIntegrationSupport {

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    // n/a
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    // n/a
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @After
  public void teardown() throws Exception {
    // n/a
  }

  /**
   * Test closing services.
   *
   * @throws Exception the exception
   */
  @Test
  public void testCloseServices() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Scan through REST API classes
  }

}
