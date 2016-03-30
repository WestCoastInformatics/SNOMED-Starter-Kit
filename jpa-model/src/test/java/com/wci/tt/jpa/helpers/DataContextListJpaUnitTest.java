/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextList;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.umls.server.helpers.ProxyTester;

/**
 * Unit testing for {@link DataContextList}.
 */
public class DataContextListJpaUnitTest extends AbstractListUnit<DataContext> {

  /** The list test fixture . */
  private DataContextList list;

  /** The list2 test fixture . */
  private DataContextList list2;

  /** The test fixture s1. */
  private DataContext dc1;

  /** The test fixture s2. */
  private DataContext dc2;

  /**
   * Setup class.
   */
  @BeforeClass
  public static void setupClass() {
    // do nothing
  }

  /**
   * Setup.
   * @throws Exception
   */
  @Before
  public void setup() throws Exception {
    list = new DataContextListJpa();
    list2 = new DataContextListJpa();

    ProxyTester tester = new ProxyTester(new DataContextJpa());
    dc1 = (DataContext) tester.createObject(1);
    dc2 = (DataContext) tester.createObject(2);

  }

  /**
   * Test normal use of a list.
   * @throws Exception the exception
   */
  @Test
  public void testNormalUse003() throws Exception {
    testNormalUse(list, list2, dc1, dc2);
  }

  /**
   * Test degenerate use of a list. Show that the underlying data structure
   * should NOT be manipulated.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUse003() throws Exception {
    testDegenerateUse(list, list2, dc1, dc2);
  }

  /**
   * Test edge cases of a list.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCases003() throws Exception {
    testEdgeCases(list, list2, dc1, dc2);
  }

  /**
   * Test XML serialization of a list.
   *
   * 
   * @throws Exception the exception
   */
  @Test
  public void testXmlSerialization003() throws Exception {
    testXmllSerialization(list, list2, dc1, dc2);
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }

  /**
   * Teardown class.
   */
  @AfterClass
  public static void teardownClass() {
    // do nothing
  }

}