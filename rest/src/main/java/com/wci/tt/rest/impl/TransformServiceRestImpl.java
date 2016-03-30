/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredDataContextList;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.helpers.DataContextListJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextListJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleListJpa;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.tt.services.handlers.SourceDataLoader;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.helpers.KeyValuePair;
import com.wci.umls.server.helpers.KeyValuePairList;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.SecurityService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Class implementation the REST Service for Transform routines for
 * {@link TransformServiceRest}.
 * 
 * Includes hibernate tags for MEME database.
 */
@Path("/transform")
@Api(value = "/transform", description = "Transformation Operations")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class TransformServiceRestImpl extends RootServiceRestImpl
    implements TransformServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link TransformServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public TransformServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /* see superclass */
  @Override
  @Path("/identify/{inputStr}")
  @POST
  @ApiOperation(value = "Identify all Providers supported data contexts", notes = "Identifies all data contexts that are supported for every Provider based on config.properties", response = ScoredDataContextListJpa.class)
  public ScoredDataContextList identify(
    @ApiParam(value = "Input text, e.g. 'oral tablet'", required = true) @PathParam("inputStr") String inputStr,
    @ApiParam(value = "Data context, e.g. Defined Customer and/or Terminology, ...", required = true) DataContextJpa context,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (Content): /identify inputStr=" + inputStr
            + ", context=" + context);

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "transform input string",
          UserRole.VIEWER);

      final List<ScoredDataContext> contexts =
          service.identify(inputStr, context);

      // Translate contexts into JPA object
      final ScoredDataContextList result = new ScoredDataContextListJpa();
      Collections.sort(contexts);
      result.setObjects(contexts);
      result.setTotalCount(contexts.size());

      return result;
    } catch (Exception e) {
      handleException(e, "trying to transform input string");
      return null;
    } finally {
      service.close();
      securityService.close();
    }

  }

  /* see superclass */
  @Override
  @Path("/process/{inputStr}")
  @POST
  @ApiOperation(value = "Process and Convert on all supported input/output data contexts", notes = "Execute the Process and Convert calls for all supported input and output contexts", response = ScoredDataContextTupleListJpa.class)
  public ScoredDataContextTupleList process(
    @ApiParam(value = "Input text, e.g. 'oral tablet'", required = true) @PathParam("inputStr") String inputStr,
    @ApiParam(value = "Input and Output Data context, e.g. List of Defined Customer and/or Terminology", required = true) DataContextListJpa inputOutputContexts,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (Content): /process inputStr=" + inputStr
            + ", inputOutputContexts=" + inputOutputContexts);

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "transform input string",
          UserRole.ADMINISTRATOR);

      // Get input/output contexts from JPA
      final DataContext inputContext = inputOutputContexts.getObjects().get(0);
      final DataContext outputContext = inputOutputContexts.getObjects().get(1);

      final List<ScoredResult> results =
          service.process(inputStr, inputContext, outputContext);

      // Translate tuples into JPA object
      final ScoredDataContextTupleList tuples =
          new ScoredDataContextTupleListJpa();

      for (final ScoredResult result : results) {
        final ScoredDataContextTuple tuple = new ScoredDataContextTupleJpa();
        tuple.setData(result.getValue());
        tuple.setScore(result.getScore());
        tuple.setDataContext(outputContext);
        tuples.getObjects().add(tuple);
      }
      Collections.sort(tuples.getObjects());
      tuples.setTotalCount(tuples.getCount());

      return tuples;
    } catch (Exception e) {
      handleException(e, "trying to transform input string");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
  }

  /* see superclass */
  @Override
  @Path("/specialties")
  @GET
  @ApiOperation(value = "Get specialties", notes = "Gets all specialty values", response = StringList.class)
  public StringList getSpecialties(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful POST call (Transform): /specialties");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get specialties",
          UserRole.VIEWER);

      final StringList list = new StringList();
      list.setObjects(service.getSpecialties());
      list.setTotalCount(list.getCount());
      return list;
    } catch (Exception e) {
      handleException(e, "get specialties");
    } finally {
      service.close();
      securityService.close();
    }
    return null;
  }

  /* see superclass */
  @Override
  @Path("/stys")
  @GET
  @ApiOperation(value = "Get semantic types", notes = "Gets all semantic type values", response = StringList.class)
  public StringList getSemanticTypes(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass()).info("RESTful POST call (Transform): /stys");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get stys", UserRole.VIEWER);

      final StringList list = new StringList();
      list.setObjects(service.getSemanticTypes());
      list.setTotalCount(list.getCount());
      return list;
    } catch (Exception e) {
      handleException(e, "get stys");
    } finally {
      service.close();
      securityService.close();
    }
    return null;
  }

  /* see superclass */
  @Override
  @Path("/data/loaders")
  @GET
  @ApiOperation(value = "Get source data loaders", notes = "Gets all source data loader key/name combinations", response = KeyValuePairList.class)
  public KeyValuePairList getSourceDataLoaders(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful POST call (Transform): /data/loaders");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get loaders", UserRole.VIEWER);

      final KeyValuePairList list = new KeyValuePairList();
      for (final Map.Entry<String, SourceDataLoader> entry : service
          .getSourceDataLoaders().entrySet()) {
        final KeyValuePair pair = new KeyValuePair();
        pair.setKey(entry.getKey());
        pair.setValue(entry.getValue().getName());
      }
      return list;
    } catch (Exception e) {
      handleException(e, "get loaders");
    } finally {
      service.close();
      securityService.close();
    }
    return null;
  }

  /* see superclass */
  @Override
  @Path("/normalizers")
  @GET
  @ApiOperation(value = "Get normalizers", notes = "Gets all normalizer key/name combinations", response = KeyValuePairList.class)
  public KeyValuePairList getNormalizers(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful POST call (Transform): /normalizers");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get normalizers",
          UserRole.VIEWER);

      final KeyValuePairList list = new KeyValuePairList();
      for (final Map.Entry<String, NormalizerHandler> entry : service
          .getNormalizers().entrySet()) {
        final KeyValuePair pair = new KeyValuePair();
        pair.setKey(entry.getKey());
        pair.setValue(entry.getValue().getName());
      }
      return list;
    } catch (Exception e) {
      handleException(e, "get normalziers");
    } finally {
      service.close();
      securityService.close();
    }
    return null;
  }

  /* see superclass */
  @Override
  @Path("/providers")
  @GET
  @ApiOperation(value = "Get providers", notes = "Gets all provider key/name combinations", response = KeyValuePairList.class)
  public KeyValuePairList getProviders(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful POST call (Transform): /providers");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get providers",
          UserRole.VIEWER);

      final KeyValuePairList list = new KeyValuePairList();
      for (final Map.Entry<String, ProviderHandler> entry : service
          .getProviders().entrySet()) {
        final KeyValuePair pair = new KeyValuePair();
        pair.setKey(entry.getKey());
        pair.setValue(entry.getValue().getName());
      }
      return list;
    } catch (Exception e) {
      handleException(e, "get provider");
    } finally {
      service.close();
      securityService.close();
    }
    return null;
  }

  /* see superclass */
  @Override
  @Path("/converters")
  @GET
  @ApiOperation(value = "Get converters", notes = "Gets all converter key/name combinations", response = KeyValuePairList.class)
  public KeyValuePairList getConverters(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful POST call (Transform): /converters");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get converters",
          UserRole.VIEWER);

      final KeyValuePairList list = new KeyValuePairList();
      for (final Map.Entry<String, ConverterHandler> entry : service
          .getConverters().entrySet()) {
        final KeyValuePair pair = new KeyValuePair();
        pair.setKey(entry.getKey());
        pair.setValue(entry.getValue().getName());
      }
      return list;
    } catch (Exception e) {
      handleException(e, "get converter");
    } finally {
      service.close();
      securityService.close();
    }
    return null;
  }

}
