/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.TypeKeyValue;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.umls.server.helpers.FieldedStringTokenizer;

/**
 * Abstract implementation of {@link NormalizerHandler}.
 * 
 * Provides utilities for key algorithms:
 * 
 * <pre>
 * 1. Acronym expansion 
 * 2. Pattern replacement
 * </pre>
 */
public abstract class AbstractNormalizer implements NormalizerHandler {

  /** The acronyms. */
  private Map<String, Set<String>> acronyms = new HashMap<>();

  /** The patterns. */
  private Map<String, String> patterns = new TreeMap<>();

  /**
   * Instantiates an empty {@link AbstractNormalizer}.
   */
  public AbstractNormalizer() {
    // n/a
  }

  /* see superclass */
  @Override
  public void addFeedback(String inputString, DataContext inputContext,
    String feedbackString) throws Exception {
    // Bail if wrong type
    if (!accepts(inputContext)) {
      return;
    }
    CoordinatorService service = new CoordinatorServiceJpa();
    try {
      // n/a
    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /* see superclass */
  @Override
  public void removeFeedback(String inputString, DataContext inputContext)
    throws Exception {
    // Bail if wrong type
    if (!accepts(inputContext)) {
      return;
    }
    CoordinatorService service = new CoordinatorServiceJpa();
    try {
      // n/a
    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /**
   * Acronym expansion delimited.
   *
   * @param inputString the input string
   * @param delim the delim
   * @param type the type
   * @return the string
   * @throws Exception the exception
   */
  public String expandAcronyms(String inputString, String delim, String type)
    throws Exception {

    // Cache acronyms
    cacheAcronyms(type);

    // set acronym identification delimters - not comma
    String[] tokens = FieldedStringTokenizer.split(inputString, delim);
    StringBuilder sb = new StringBuilder();
    for (final String token : tokens) {
      if (token.isEmpty()) {
        continue;
      }
      if (acronyms.containsKey(token)) {
        for (final String expansion : acronyms.get(token)) {
          sb.append(expansion).append(" ");
        }
      } else {
        sb.append(token).append(" ");
      }
    }
    return sb.toString().trim();
  }

  /**
   * Replace patterns.
   *
   * @param inputString the input string
   * @param type the type
   * @return the string
   * @throws Exception the exception
   */
  public String replacePatterns(String inputString, String type)
    throws Exception {
    // Cache patterns
    cachePatterns(type);
    // matching
    String replString = inputString;
    for (final String match : patterns.keySet()) {
      final String repl = patterns.get(match);
      // Case insensitive match - put word boundaries into patterns if needed
      replString = replString.replaceAll("(?i)" + match, repl);
    }
    return replString;
  }

  /**
   * Cache acronyms.
   *
   * @param type the type
   * @throws Exception the exception
   */
  private void cacheAcronyms(String type) throws Exception {
    if (!acronyms.isEmpty()) {
      return;
    }
    CoordinatorService service = new CoordinatorServiceJpa();
    try {

      for (final TypeKeyValue tvk : service
          .findTypeKeyValuesForQuery("type:" + type)) {
        if (!acronyms.containsKey(tvk.getKey())) {
          acronyms.put(tvk.getKey(), new HashSet<String>(2));
        }
        acronyms.get(tvk.getKey()).add(tvk.getValue());
      }
    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /**
   * Cache patterns.
   *
   * @param type the type
   * @throws Exception the exception
   */
  private void cachePatterns(String type) throws Exception {
    if (!patterns.isEmpty()) {
      return;
    }
    CoordinatorService service = new CoordinatorServiceJpa();
    try {

      for (final TypeKeyValue tvk : service
          .findTypeKeyValuesForQuery("type:" + type)) {
        patterns.put(tvk.getKey(), tvk.getValue());
      }
    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

}
