/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.ssk.rest.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.rest.impl.ConfigureServiceRestImpl;
import com.wci.umls.server.rest.impl.ContentServiceRestImpl;
import com.wci.umls.server.rest.impl.HistoryServiceRestImpl;
import com.wci.umls.server.rest.impl.MetadataServiceRestImpl;
import com.wci.umls.server.rest.impl.ProjectServiceRestImpl;
import com.wci.umls.server.rest.impl.SecurityServiceRestImpl;
import com.wci.umls.server.rest.impl.SourceDataServiceRestImpl;
import com.wordnik.swagger.jaxrs.config.BeanConfig;

/**
 * The application (for jersey). Also serves the role of the initialization
 * listener.
 */
@ApplicationPath("/")
public class SnomedStarterKitServerApplication extends Application {

  /** The API_VERSION - also used in "swagger.htmL" */
  public final static String API_VERSION = "1.0.0";

  /** The timer. */
  Timer timer;

  /**
   * Instantiates an empty {@link SnomedStarterKitServerApplication}.
   *
   * @throws Exception the exception
   */
  public SnomedStarterKitServerApplication() throws Exception {
    Logger.getLogger(getClass())
        .info("WCI SNOMED Starter Kit APPLICATION START");
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setTitle("WCI SNOMED Starter Kit service API");
    beanConfig.setDescription("RESTful calls for WCI SNOMED Starter Kit");
    beanConfig.setVersion(API_VERSION);
    if (new ConfigureServiceRestImpl().isConfigured()) {
      beanConfig.setBasePath(
          ConfigUtility.getConfigProperties().getProperty("base.url"));
      // TODO Change this to SSK?
      beanConfig.setResourcePackage("com.wci.umls.server.rest.impl");
      beanConfig.setScan(true);
    }

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
    classes.add(MetadataServiceRestImpl.class);
    classes.add(HistoryServiceRestImpl.class);
    classes.add(ContentServiceRestImpl.class);
    classes.add(SourceDataServiceRestImpl.class);
    classes.add(ProjectServiceRestImpl.class);
    classes.add(ConfigureServiceRestImpl.class);
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
    // instances.add(new LoggingFilter());
    return instances;
  }

}