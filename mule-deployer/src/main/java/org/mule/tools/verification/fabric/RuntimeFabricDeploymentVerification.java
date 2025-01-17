/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.verification.fabric;

import org.apache.commons.lang3.StringUtils;
import org.mule.tools.client.core.exception.DeploymentException;
import org.mule.tools.client.fabric.RuntimeFabricClient;
import org.mule.tools.client.fabric.model.DeploymentDetailedResponse;
import org.mule.tools.client.fabric.model.DeploymentGenericResponse;
import org.mule.tools.client.fabric.model.Deployments;
import org.mule.tools.deployment.fabric.RequestBuilder;
import org.mule.tools.model.Deployment;
import org.mule.tools.model.anypoint.RuntimeFabricDeployment;
import org.mule.tools.verification.DefaultDeploymentVerification;
import org.mule.tools.verification.DeploymentVerification;
import org.mule.tools.verification.DeploymentVerificationStrategy;

import com.google.gson.JsonArray;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RuntimeFabricDeploymentVerification implements DeploymentVerification {

  private final RuntimeFabricClient client;
  private DefaultDeploymentVerification verification;

  private static final String FAILED_STATUS = "FAILED";
  private static final String APPLIED_STATUS = "APPLIED";
  private static final String STARTED_STATUS = "STARTED";

  public RuntimeFabricDeploymentVerification(RuntimeFabricClient client) {
    this.client = client;
    this.verification = new DefaultDeploymentVerification(new RuntimeFabricDeploymentVerificationStrategy());
  }

  @Override
  public void assertDeployment(Deployment deployment) throws DeploymentException {
    verification.assertDeployment(deployment);
  }

  private class RuntimeFabricDeploymentVerificationStrategy implements DeploymentVerificationStrategy {

    private Deployments deployments;
    private String deploymentId;

    private RuntimeFabricDeploymentVerificationStrategy() {
      deployments = client.getDeployments();
    }

    @Override

    public Predicate<Deployment> isDeployed() {
      return (deployment) -> {
        try {
          String deploymentId;

          deploymentId = getDeploymentId(deployment);

          if (deploymentId == null) {
            return false;
          }
          DeploymentDetailedResponse response = client.getDeployment(deploymentId);
          if (response != null) {
            if (StringUtils.equals(response.status, FAILED_STATUS)) {
              throw new IllegalStateException("Deployment failed");
            } else if (StringUtils.equals(response.status, APPLIED_STATUS)
                || StringUtils.equals(response.status, STARTED_STATUS)) {
              return true;
            }
          }
        } catch (DeploymentException e) {
          e.printStackTrace();
          return false;
        }
        return false;
      };
    }

    private String getDeploymentId(Deployment deployment) throws DeploymentException {
      RuntimeFabricDeployment deploymentRTF = (RuntimeFabricDeployment) deployment;
      String targetName = deploymentRTF.getTarget();
      JsonArray targets = client.getTargets();
      String targetID = null;
      if (targets != null) {
        targetID = RequestBuilder.getTargetId(targets, targetName);
      }
      if (deploymentId == null) {
        deployments = client.getDeployments();
        for (DeploymentGenericResponse dep : deployments) {
          if (StringUtils.equals(dep.name, deploymentRTF.getApplicationName()) && (targets == null ||
              StringUtils.equals(targetID, dep.target.targetId))) {
            deploymentId = dep.id;
            return deploymentId;
          }
        }
      }
      return deploymentId;
    }

    @Override
    public Consumer<Deployment> onTimeout() {
      return deployment -> {
        try {
          client.getDeployment(getDeploymentId(deployment));
        } catch (DeploymentException e) {
          e.printStackTrace();
        }
      };
    }
  }
}
