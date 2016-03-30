/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.log4j.Logger;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.rest.impl.SecurityServiceRestImpl;
import com.wordnik.swagger.jaxrs.config.BeanConfig;

/**
 * The application (for jersey). Also serves the role of the initialization
 * listener.
 */
@ApplicationPath("/")
public class TerminologyTransformerServerApplication extends Application {

  /** The API_VERSION - also used in "swagger.htmL" */
  public final static String API_VERSION = "1.0.0";

  /** The timer. */
  Timer timer;

  /**
   * Instantiates an empty {@link TerminologyTransformerServerApplication}.
   *
   * @throws Exception the exception
   */
  public TerminologyTransformerServerApplication() throws Exception {
    Logger.getLogger(getClass())
        .info("WCI Terminology Transformer APPLICATION START");
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setTitle("WCI Terminology Transformer service API");
    beanConfig.setDescription("RESTful calls for WCI Terminology Transformer");
    beanConfig.setVersion(API_VERSION);
    beanConfig.setBasePath(
        ConfigUtility.getConfigProperties().getProperty("base.url"));
    beanConfig.setResourcePackage("com.wci.tt.rest.impl");
    beanConfig.setScan(true);

    // Set up a timer task to run at 2AM every day
    // TimerTask task = new InitializationTask();
    // timer = new Timer();
    // Calendar today = Calendar.getInstance();
    // today.set(Calendar.HOUR_OF_DAY, 2);
    // today.set(Calendar.MINUTE, 0);
    // today.set(Calendar.SECOND, 0);
    // //timer.scheduleAtFixedRate(task, today.getTime(), 6 * 60 * 60 * 1000);

  }

  /* see superclass */
  @Override
  public Set<Class<?>> getClasses() {
    final Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.add(SecurityServiceRestImpl.class);
    classes.add(SourceDataServiceRestImpl.class);
    classes.add(TransformServiceRestImpl.class);
    classes
        .add(com.wordnik.swagger.jersey.listing.ApiListingResourceJSON.class);
    classes.add(
        com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider.class);
    classes.add(
        com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider.class);
    return classes;
  }

  /* see superclass */
  @Override
  public Set<Object> getSingletons() {
    final Set<Object> instances = new HashSet<Object>();
    instances.add(new JacksonFeature());
    instances.add(new JsonProcessingFeature());
    instances.add(new MultiPartFeature());
    // Enable for LOTS of logging of HTTP requests
    instances.add(new LoggingFilter());
    return instances;
  }

}