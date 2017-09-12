/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.client.agent;

import groovy.util.ScriptException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.mule.tools.client.AbstractDeployer;
import org.mule.tools.client.exception.ClientException;
import org.mule.tools.client.standalone.exception.DeploymentException;

import org.mule.tools.model.DeployerLog;
import org.mule.tools.model.DeploymentConfiguration;

public class AgentDeployer extends AbstractDeployer {

  private AgentClient agentClient;

  public AgentDeployer(DeploymentConfiguration deploymentConfiguration, DeployerLog log) throws DeploymentException {
    super(deploymentConfiguration, log);
  }

  @Override
  public void deploy() throws DeploymentException {
    try {
      info("Deploying application " + getApplicationName() + " to Mule Agent");
      agentClient.deployApplication(getApplicationName(), getApplicationFile());
    } catch (ClientException e) {
      error("Failure: " + e.getMessage());
      throw new DeploymentException("Failed to deploy application " + getApplicationName(), e);
    }
  }

  @Override
  public void undeploy(MavenProject mavenProject) throws DeploymentException {
    AgentClient agentClient = new AgentClient(log, deploymentConfiguration.getUri());
    log.info("Undeploying application " + deploymentConfiguration.getApplicationName());
    agentClient.undeployApplication(deploymentConfiguration.getApplicationName());
  }

  @Override
  protected void initialize() {
    this.agentClient = new AgentClient(log, deploymentConfiguration.getUri());
  }

  @Override
  public void resolveDependencies(MavenProject mavenProject, ArtifactResolver artifactResolver, ArchiverManager archiverManager,
                                  ArtifactFactory artifactFactory, ArtifactRepository localRepository)
      throws DeploymentException, ScriptException {

  }

}