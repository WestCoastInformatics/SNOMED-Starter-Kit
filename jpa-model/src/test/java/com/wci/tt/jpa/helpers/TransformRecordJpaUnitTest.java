/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.TestSupport;
import com.wci.tt.jpa.TransformRecordJpa;
import com.wci.umls.server.helpers.CopyConstructorTester;
import com.wci.umls.server.helpers.EqualsHashcodeTester;
import com.wci.umls.server.helpers.GetterSetterTester;
import com.wci.umls.server.helpers.ProxyTester;
import com.wci.umls.server.helpers.XmlSerializationTester;
import com.wci.umls.server.jpa.helpers.IndexedFieldTester;
import com.wci.umls.server.jpa.helpers.NullableFieldTester;

/**
 * Unit testing for {@link TransformRecordJpa}.
 */
public class TransformRecordJpaUnitTest extends TestSupport {

  /** The model object to test. */
  private TransformRecordJpa object;

  /** The test fixture dc1. */
  private DataContextJpa dc1;

  /** The test fixture dc2. */
  private DataContextJpa dc2;

  /** The l1 test fixture. */
  private List<ScoredResult> l1;

  /** The l2 test fixture. */
  private List<ScoredResult> l2;

  /** The m1 test fixture. */
  private Map<String, String> m1;

  /** The m2 test fixture. */
  private Map<String, String> m2;

  /**
   * Setup class.
   */
  @BeforeClass
  public static void setupClass() {
    // do nothing
  }

  /**
   * Setup.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    object = new TransformRecordJpa();

    ProxyTester tester = new ProxyTester(new DataContextJpa());
    dc1 = (DataContextJpa) tester.createObject(1);
    dc2 = (DataContextJpa) tester.createObject(2);

    l1 = new ArrayList<ScoredResult>();
    l1.add(new ScoredResultJpa("1", 1.0f));
    l2 = new ArrayList<ScoredResult>();
    l1.add(new ScoredResultJpa("2", 2.0f));

    m1 = new HashMap<>();
    m1.put("1", null);
    m2 = new HashMap<>();
    m2.put("2", null);
  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    GetterSetterTester tester = new GetterSetterTester(object);
    tester.test();
  }

  /**
   * Test equals and hashcode methods.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelEqualsHashcode() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("characteristics");
    tester.include("inputContext");
    tester.include("inputString");
    tester.include("normalizedResults");
    tester.include("outputContext");
    tester.include("providerOutputContext");
    tester.include("outputs");
    tester.include("statistics");

    tester.proxy(DataContext.class, 1, dc1);
    tester.proxy(DataContext.class, 2, dc2);
    tester.proxy(List.class, 1, l1);
    tester.proxy(List.class, 2, l2);
    tester.proxy(Map.class, 1, m1);
    tester.proxy(Map.class, 2, m2);
    assertTrue(tester.testIdentityFieldEquals());
    assertTrue(tester.testNonIdentityFieldEquals());
    assertTrue(tester.testIdentityFieldNotEquals());
    assertTrue(tester.testIdentityFieldHashcode());
    assertTrue(tester.testNonIdentityFieldHashcode());
    assertTrue(tester.testIdentityFieldDifferentHashcode());
  }

  /**
   * Test copy constructor.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelCopy() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    CopyConstructorTester tester = new CopyConstructorTester(object);
    tester.proxy(DataContext.class, 1, dc1);
    tester.proxy(DataContext.class, 2, dc2);
    tester.proxy(List.class, 1, l1);
    tester.proxy(List.class, 2, l2);
    tester.proxy(Map.class, 1, m1);
    tester.proxy(Map.class, 2, m2);
    assertTrue(tester.testCopyConstructor(TransformRecord.class));
  }

  /**
   * Test XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlSerialization() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    XmlSerializationTester tester = new XmlSerializationTester(object);
    tester.proxy(DataContext.class, 1, dc1);
    tester.proxy(DataContext.class, 2, dc2);
    tester.proxy(List.class, 1, l1);
    tester.proxy(List.class, 2, l2);
    tester.proxy(Map.class, 1, m1);
    tester.proxy(Map.class, 2, m2);
    assertTrue(tester.testXmlSerialization());
  }

  /**
   * Test not null fields.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelNotNullField() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    NullableFieldTester tester = new NullableFieldTester(object);
    tester.include("inputString");
    tester.include("timestamp");
    tester.include("lastModifiedBy");
    tester.include("lastModified");
    assertTrue(tester.testNotNullFields());
  }

  /**
   * Test field indexing.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelIndexedFields() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());

    // Test analyzed fields
    IndexedFieldTester tester = new IndexedFieldTester(object);
    tester.include("inputString");
    assertTrue(tester.testAnalyzedIndexedFields());

    // Test non analyzed fields
    tester = new IndexedFieldTester(object);
    tester.include("id");
    tester.include("lastModified");
    tester.include("lastModifiedBy");
    tester.include("inputStringSort");
    assertTrue(tester.testNotAnalyzedIndexedFields());

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