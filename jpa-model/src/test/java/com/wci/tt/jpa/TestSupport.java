/*
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa;

import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Support object for unit tests.
 */
public class TestSupport {

  /** The name. */
  @Rule
  public TestName name = new TestName();

}