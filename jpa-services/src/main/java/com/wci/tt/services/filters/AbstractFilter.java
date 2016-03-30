/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.filters;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.wci.tt.helpers.TypeKeyValue;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.services.CoordinatorService;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * Abstract {@link Filter} to be used as a superclass.
 */
public abstract class AbstractFilter {

  /** The config map. */
  private Map<String, Map<String, String>> configMap = new HashMap<>();

  /** Filtered results. */
  private Map<String, List<String>> filteredMap = new HashMap<>();

  /** The writer map. */
  private Map<String, PrintWriter> writerMap = new HashMap<>();

  private String punctuationRegex =
      ConfigUtility.PUNCTUATION_REGEX.replace(" ", "");

  /**
   * Cache type.
   *
   * @param type the type
   * @throws Exception the exception
   */
  protected void cacheType(String type) throws Exception {
    if (!configMap.isEmpty()) {
      return;
    }
    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      configMap.put(type, new HashMap<String, String>());
      final Map<String, String> keyValueMap = configMap.get(type);
      for (final TypeKeyValue tkv : service
          .findTypeKeyValuesForQuery("type:" + type)) {
        keyValueMap.put(tkv.getKey(), tkv.getValue());
      }

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /**
   * Returns the value.
   *
   * @param type the type
   * @param key the key
   * @return the value
   */
  protected String getValue(String type, String key) {
    return configMap.get(type).get(key);
  }

  /**
   * Exists.
   *
   * @param type the type
   * @param key the key
   * @return true, if successful
   */
  protected boolean exists(String type, String key) {
    return configMap.get(type).containsKey(key);
  }

  /**
   * Adds the filtered result.
   *
   * @param category the category
   * @param inputStr the input str
   * @throws Exception the exception
   */
  protected void addFilteredResult(String category, String inputStr)
    throws Exception {
    if (!filteredMap.containsKey(category)) {
      filteredMap.put(category, new ArrayList<String>());
    }
    filteredMap.get(category).add(inputStr);

    // Write the result to a file if filters.directory.output is set
    // and analysis.mode is set
    final Properties prop = ConfigUtility.getConfigProperties();
    if (ConfigUtility.isAnalysisMode()
        && prop.containsKey("filters.directory.output")) {

      if (!writerMap.containsKey(category)) {
        final File file =
            new File(prop.getProperty("filters.directory.output").toString()
                + "/" + "filter-" + category + ".txt");
        if (file.exists()) {
          file.delete();
        }
        final PrintWriter out = new PrintWriter(new FileWriter(file));
        writerMap.put(category, out);
      }
      writerMap.get(category).println(inputStr);
      writerMap.get(category).flush();
    }

  }

  /**
   * Returns the filtered results.
   *
   * @param type the type
   * @return the filtered results
   */
  protected List<String> getFilteredResults(String type) {
    return filteredMap.get(type);
  }

  /**
   * Checks whether the input string is filtered for the specified type. If so,
   * adds a filtered result and returns true. Otherwise it returns false.
   * 
   * Filters on: 1) Exact matches (to handle blacklists) 2) Word-based
   * instances(to handle specified keywords or types)
   *
   * @param type the type
   * @param inputStr the input str
   * @return true, if successful
   * @throws Exception the exception
   */
  protected boolean checkFilterType(String type, String inputStr)
    throws Exception {
    boolean filterRequired = false;
    String category = null;

    if (configMap.get(type).containsKey(inputStr)) {
      // Exact match found
      filterRequired = true;
      category = configMap.get(type).get(inputStr);
    } else {
      // Look for keyword matches
      for (String item : configMap.get(type).keySet()) {
        String matchWord = item.toLowerCase().replaceAll("\\p{P}", "");
        matchWord = ".*\\b" + matchWord.toLowerCase() + "\\b.*";
        if (inputStr.replaceAll(punctuationRegex, " ").toLowerCase().trim()
            .matches(matchWord)) {
          filterRequired = true;
          category = configMap.get(type).get(item);
          break;
        }
      }
    }

    if (filterRequired) {
      // Must be filtered, add to set of filtered results
      addFilteredResult(category, inputStr);
    }

    return filterRequired;
  }

  /**
   * Close.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception {
    for (final PrintWriter out : writerMap.values()) {
      out.close();
    }
  }

}
