/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.wci.tt.helpers.TypeKeyValue;
import com.wci.tt.jpa.helpers.TypeKeyValueJpa;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.services.CoordinatorService;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.services.handlers.ExceptionHandler;

/**
 * Used to for loading/reloading configuration data.
 * 
 * See admin/pom.xml for a sample execution.
 * 
 * @goal load-config
 * @phase package
 */
public class ConfigLoaderMojo extends AbstractMojo {

  /**
   * The "type" value to use
   * @parameter
   */
  private String type;

  /**
   * The config input file
   * @parameter
   * @required
   */
  private String inputFile;

  /**
   * The reload flag
   * @parameter
   * @required
   */
  private boolean reload;

  /**
   * Executes the plugin.
   *
   * @throws MojoExecutionException the mojo execution exception
   * @throws MojoFailureException the mojo failure exception
   */
  @SuppressWarnings("resource")
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Starting sample data load");
    getLog().info("  type = " + type);
    getLog().info("  inputFile = " + inputFile);
    getLog().info("  reload = " + reload);

    CoordinatorService service = null;
    try {

      service = new CoordinatorServiceJpa();
      service.setTransactionPerOperation(false);
      service.beginTransaction();

      // As this is a sample loader and not an integration test,
      // we will use the JPA service layer directly.

      if (inputFile == null) {
        throw new Exception("Input file not specified");
      }

      final File file = new File(inputFile);
      if (!file.exists()) {
        throw new Exception("Input file does not exist");
      }

      // If reload, remove all with type
      if (reload) {
        for (final TypeKeyValue tkv : service
            .findTypeKeyValuesForQuery("type:" + type)) {
          service.removeTypeKeyValue(tkv.getId());
        }
      }

      // Open file
      final BufferedReader in =
          new BufferedReader(new FileReader(new File(inputFile)));
      String line = null;
      while ((line = in.readLine()) != null) {
        final String[] tokens = FieldedStringTokenizer.split(line, "\t");
        if (tokens.length > 2) {
          throw new Exception(
              "Unexpected number of fields in config file: " + tokens.length);
        }

        // First field is "key", second field is "value".
        TypeKeyValue tkv = new TypeKeyValueJpa();
        tkv.setType(type);
        tkv.setKey(tokens[0]);
        if (tokens.length > 1) {
          tkv.setValue(tokens[1]);
        } else {
          tkv.setValue("");
        }
        service.addTypeKeyValue(tkv);
      }
      in.close();

      service.commit();
      getLog().info("Done ...");

    } catch (Exception e) {
      // Send email if something went wrong
      try {
        ExceptionHandler.handleException(e, "Error loading RF2 source data");
      } catch (Exception e1) {
        e1.printStackTrace();
        throw new MojoFailureException(e.getMessage());
      }

    } finally {
      // Close service(s)
      if (service != null) {
        try {
          service.close();
        } catch (Exception e) {
          // n/a
        }
      }
    }

  }
}