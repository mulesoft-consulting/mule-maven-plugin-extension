/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.deployment.cloudhub2;

import org.mule.tools.client.core.exception.DeploymentException;
import org.mule.tools.client.fabric.RuntimeFabricClient;
import org.mule.tools.client.fabric.model.ApplicationModify;
import org.mule.tools.client.fabric.model.ApplicationRequest;
import org.mule.tools.client.fabric.model.AssetReference;
import org.mule.tools.client.fabric.model.DeploymentRequest;
import org.mule.tools.client.fabric.model.Target;
import org.mule.tools.model.anypoint.Cloudhub2Deployment;

import java.util.HashMap;
import java.util.Map;

public class RequestBuilderCh2 extends org.mule.tools.deployment.fabric.RequestBuilder {

  public static final String ID = "id";

  protected RequestBuilderCh2(Cloudhub2Deployment deployment, RuntimeFabricClient client) {
    super(deployment, client);
  }

  public DeploymentRequest buildDeploymentRequest() throws DeploymentException {
    ApplicationRequest applicationRequest = buildApplicationRequest();

    Target target = buildTarget();

    DeploymentRequest deploymentRequest = new DeploymentRequest();

    deploymentRequest.setName(deployment.getApplicationName());
    deploymentRequest.setApplication(applicationRequest);
    deploymentRequest.setTarget(target);

    applicationRequest.setConfiguration(createConfiguration());
    applicationRequest.setvCores(((Cloudhub2Deployment) deployment).getvCores());
    applicationRequest.setIntegrations(((Cloudhub2Deployment) deployment).getIntegrations());
    return deploymentRequest;
  }

  @Override
  protected ApplicationModify buildApplicationModify() {
    AssetReference assetReference = buildAssetReference();
    ApplicationModify applicationModify = new ApplicationModify();
    applicationModify.setRef(assetReference);
    applicationModify.setConfiguration(createConfiguration());
    applicationModify.setvCores(((Cloudhub2Deployment) deployment).getvCores());
    return applicationModify;
  }

  public Object createConfiguration() {
    Map<String, Object> properties = new HashMap<>();
    if (deployment.getProperties() != null) {
      properties.put("properties", deployment.getProperties());
    }
    if (deployment.getSecureProperties() != null) {
      properties.put("secureProperties", deployment.getSecureProperties());
    }
    properties.put("applicationName", deployment.getApplicationName());
    Map<String, Object> loggingServiceProperties = null;
    if (((Cloudhub2Deployment) deployment).getScopeLoggingConfigurations() != null) {
      loggingServiceProperties = new HashMap<>();
      loggingServiceProperties.put("artifactName", deployment.getApplicationName());
      loggingServiceProperties.put("scopeLoggingConfigurations",
                                   ((Cloudhub2Deployment) deployment).getScopeLoggingConfigurations());
    }
    return new Cloudhub2Configuration(properties, loggingServiceProperties);

  }

}
