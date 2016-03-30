/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.helper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.jpa.DataContextJpa;

/**
 * Helper utility for specifying data contexts to match.
 */
public class DataContextMatcher {

  /** The contexts to match against, null fields are wildcards. */
  private Set<DataContext> contexts = new HashSet<>();

  /**
   * Instantiates an empty {@link DataContextMatcher}.
   */
  public DataContextMatcher() {
    // n/a
  }

  /**
   * Returns the data contexts.
   *
   * @return the data contexts
   */
  public List<DataContext> getDataContexts() {
    return new ArrayList<DataContext>(contexts);
  }

  /**
   * Indicates whether the specified context matches the previously configured
   * parameters.
   *
   * @param context the context
   * @return true, if successful
   */
  public boolean matches(DataContext context) {
    Logger.getLogger(getClass()).debug("Match context - " + context);

    // null type never matches
    if (context.getType() == null) {
      return false;
    }
    boolean matches = false;
    for (final DataContext matchContext : contexts) {
      if (matches(context, matchContext)) {
        matches = true;
        break;
      }
    }
    Logger.getLogger(getClass()).debug("  match = " + matches);
    return matches;
  }

  /**
   * Indicate whether the specified contexts match each other, taking into
   * account the wildcard settings on either side.
   *
   * @param actual the context1
   * @param match the context2
   * @return true, if successful
   */
  public static boolean matches(DataContext actual, DataContext match) {
    if (actual.getType() != match.getType()) {
      return false;
    }

    if (!wildcardMatch(actual.getCustomer(), match.getCustomer())) {
      return false;
    }
    if (!wildcardMatch(actual.getSemanticType(), match.getSemanticType())) {
      return false;
    }
    if (!wildcardMatch(actual.getSpecialty(), match.getSpecialty())) {
      return false;
    }
    if (!wildcardMatch(actual.getInfoModelClass(), match.getInfoModelClass())) {
      return false;
    }
    if (!wildcardMatch(actual.getTerminology(), match.getTerminology())) {
      return false;
    }
    if (!wildcardMatch(actual.getVersion(), match.getVersion())) {
      return false;
    }
    // otherwise true
    return true;
  }

  /**
   * Indicates whether two strings match, taking wildcard logic into account
   *
   * @param s1 the s1
   * @param s2 the s2
   * @return true, if successful
   */
  private static boolean wildcardMatch(String s1, String s2) {
    // values must match
    if (s1 != null && s2 != null && !s1.equals(s2)) {
      return false;
    }
    // if s2 is required, so it is for s1
    if (s1 == null && s2 != null) {
      return false;
    }
    return true;
  }

  /**
   * Configures this matcher to identify the specified context. A null value
   * indicates a wildcard.
   *
   * @param context the context
   * @throws Exception the exception
   */
  public void configureContext(DataContext context) throws Exception {
    configureContext(context.getType(), context.getCustomer(),
        context.getSemanticType(), context.getSpecialty(),
        context.getInfoModelClass(), context.getTerminology(),
        context.getVersion());
  }

  /**
   * Configures this matcher to identify the specified context parameters. A
   * null vaaue indicates a wildcard.
   *
   * @param type the type
   * @param customer the customer
   * @param semanticType the semantic type
   * @param specialty the specialty
   * @param infoModelClass the info model name
   * @param terminology the terminology
   * @param version the version
   * @throws Exception the exception
   */
  public void configureContext(DataContextType type, String customer,
    String semanticType, String specialty, String infoModelClass,
    String terminology, String version) throws Exception {

    if (type == null) {
      throw new Exception("A null type cannot be supplied");
    }

    final DataContext context = new DataContextJpa();
    context.setType(type);
    context.setCustomer(customer);
    context.setSemanticType(semanticType);
    context.setSpecialty(specialty);
    context.setInfoModelClass(infoModelClass);
    context.setTerminology(terminology);
    context.setVersion(version);
    Logger.getLogger(getClass())
        .debug("Matcher configure context - " + context);
    contexts.add(context);

  }

  /**
   * Configures this matcher to identify the specified context parameters. A
   * null value should be used to indicate a wildcard.
   *
   * @param types the types
   * @param customers the customers
   * @param semanticTypes the semantic types
   * @param specialties the specialties
   * @param infoModelClasses the info model classes
   * @param terminologies the terminologies
   * @param versions the versions
   * @throws Exception the exception
   */
  public void configureContext(EnumSet<DataContextType> types,
    List<String> customers, List<String> semanticTypes,
    List<String> specialties, List<String> infoModelClasses,
    List<String> terminologies, List<String> versions) throws Exception {

    // Here, compose every possible combination of the attached parameters.
    // A null value corresponds to a wildcard
    if (types == null || types.isEmpty()) {
      throw new Exception("At least one type must be supplied");
    }

    // Handle wildcard setup
    if (customers == null) {
      customers = new ArrayList<>();
      customers.add("*");
    } else if (customers.isEmpty()) {
      throw new Exception(
          "Empty customer list is not allowed, pass null for wildcard.");
    }

    if (semanticTypes == null) {
      semanticTypes = new ArrayList<>();
      semanticTypes.add("*");
    } else if (semanticTypes.isEmpty()) {
      throw new Exception(
          "Empty semantic type list is not allowed, pass null for wildcard.");
    }

    if (specialties == null) {
      specialties = new ArrayList<>();
      specialties.add("*");
    } else if (specialties.isEmpty()) {
      throw new Exception(
          "Empty specialty list is not allowed, pass null for wildcard.");
    }

    if (infoModelClasses == null) {
      infoModelClasses = new ArrayList<>();
      infoModelClasses.add("*");
    } else if (infoModelClasses.isEmpty()) {
      throw new Exception(
          "Empty info model name list is not allowed, pass null for wildcard.");
    }

    if (terminologies == null) {
      terminologies = new ArrayList<>();
      terminologies.add("*");
    } else if (terminologies.isEmpty()) {
      throw new Exception(
          "Empty terminology list is not allowed, pass null for wildcard.");
    }

    if (versions == null) {
      versions = new ArrayList<>();
      versions.add("*");
    } else if (versions.isEmpty()) {
      throw new Exception(
          "Empty version list is not allowed, pass null for wildcard.");
    }

    // Add all possible combinations (seems combinatorially problematic
    // but in practice, most of these parameters will have 1 entry
    for (final DataContextType type : types) {
      for (final String customer : customers) {
        for (final String semanticType : semanticTypes) {
          for (final String specialty : specialties) {
            for (final String infoModelClass : infoModelClasses) {
              for (final String terminology : terminologies) {
                for (final String version : versions) {
                  final DataContext context = new DataContextJpa();
                  context.setType(type);
                  context.setCustomer(customer.equals("*") ? null : customer);
                  context.setSemanticType(
                      semanticType.equals("*") ? null : semanticType);
                  context
                      .setSpecialty(specialty.equals("*") ? null : specialty);
                  context.setInfoModelClass(
                      infoModelClass.equals("*") ? null : infoModelClass);
                  context.setTerminology(
                      terminology.equals("*") ? null : terminology);
                  context.setVersion(version.equals("*") ? null : version);
                  Logger.getLogger(getClass())
                      .debug("Matcher configure context - " + context);
                  contexts.add(context);
                }
              }
            }
          }
        }
      }
    }

  }

  @Override
  public String toString() {
    return "DataContextMatcher [contexts=" + contexts + "]";
  }

}
