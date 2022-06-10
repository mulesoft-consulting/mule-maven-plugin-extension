/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.client.cloudhub.model;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Mulesoft Inc.
 * @since 2.3.0
 * added @parameter since 3.7
 */
public class LogLevelInfo {

  @Parameter
  private String packageName;

  @Parameter
  private LogLevel level;

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public LogLevel getLevel() {
    return level;
  }

  public void setLevel(LogLevel level) {
    this.level = level;
  }
}
