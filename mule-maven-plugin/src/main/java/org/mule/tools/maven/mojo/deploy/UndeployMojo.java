/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.maven.mojo.deploy;

import org.mule.tools.client.AbstractDeployer;
import org.mule.tools.client.DeployerFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.mule.tools.client.standalone.exception.DeploymentException;
import org.mule.tools.maven.mojo.deploy.logging.MavenDeployerLog;

/**
 * Undeploys all the applications on a Mule Runtime Standalone server, regardless of whether it was started using start or deploy
 * goals.
 *
 * @see DeployMojo
 * @since 1.0
 */
@Mojo(name = "undeploy", requiresProject = true)
public class UndeployMojo extends AbstractMuleDeployerMojo {

  @Override
  public void execute() throws MojoFailureException, MojoExecutionException {
    super.execute();
    try {
      AbstractDeployer deployer =
          new DeployerFactory().createDeployer(deploymentConfiguration, new MavenDeployerLog(getLog()));
      deployer.undeploy(mavenProject);
    } catch (DeploymentException e) {
      getLog().error("Failed to undeploy " + deploymentConfiguration.getApplicationName() + ": " + e.getMessage(), e);
      throw new MojoFailureException("Failed to undeploy [" + deploymentConfiguration.getApplication() + "]");
    }
  }
}