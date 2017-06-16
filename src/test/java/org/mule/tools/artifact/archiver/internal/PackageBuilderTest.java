/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tools.artifact.archiver.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PackageBuilderTest {

  private File destinationFileMock;
  private PackageBuilder packageBuilder;

  @Rule
  public TemporaryFolder targetFileFolder = new TemporaryFolder();

  @Before
  public void beforeTest() {
    this.packageBuilder = new PackageBuilder();
    this.destinationFileMock = mock(File.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setNullClassesFolderTest() {
    this.packageBuilder.withClasses(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setNullMuleFolderTest() {
    this.packageBuilder.withMule(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setNullRootResourceFileTest() {
    this.packageBuilder.withRootResource(null);
  }

  @Test
  public void addRootResourcesTest() throws NoSuchFieldException, IllegalAccessException {
    Class<?> clazz = this.packageBuilder.getClass();
    Field field = clazz.getDeclaredField("rootResources");
    field.setAccessible(true);
    List<File> actualRootResourcesList = (List<File>) field.get(this.packageBuilder);

    Assert.assertTrue("The list of root resources should be empty", actualRootResourcesList.isEmpty());
    this.packageBuilder.withRootResource(mock(File.class));
    Assert.assertEquals("The list of root resources should contain one element", 1, actualRootResourcesList.size());
    this.packageBuilder.withRootResource(mock(File.class));
    Assert.assertEquals("The list of root resources should contain two elements", 2, actualRootResourcesList.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setNullDestinationFileTest() {
    this.packageBuilder.withDestinationFile(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setExistentDestinationFileTest() {
    when(destinationFileMock.exists()).thenReturn(true);
    this.packageBuilder.withDestinationFile(destinationFileMock);
  }

  @Test
  public void setDestinationFileTest() {
    when(destinationFileMock.exists()).thenReturn(false);
    this.packageBuilder.withDestinationFile(destinationFileMock);
    verify(destinationFileMock, times(1)).exists();
  }

  @Test(expected = NullPointerException.class)
  public void setNullArchiverTest() {
    this.packageBuilder.withArchiver(null);
  }

  @Test
  public void setArchiverTest() {
    Class expectedDefaultMuleArchiverClass = MuleArchiver.class;
    Class actualDefaultMuleArchiverClass = this.packageBuilder.getMuleArchiver().getClass();
    Assert.assertEquals("Expected and actual default mule org.mule.tools.artifact.archiver does not match",
                        expectedDefaultMuleArchiverClass, actualDefaultMuleArchiverClass);

    class MuleArchiverSubclass extends MuleArchiver {
    };
    Class expectedMuleArchiverClass = MuleArchiverSubclass.class;
    this.packageBuilder.withArchiver(new MuleArchiverSubclass());
    Class actualMuleArchiverClass = this.packageBuilder.getMuleArchiver().getClass();
    Assert.assertEquals("Expected and actual mule org.mule.tools.artifact.archiver does not match", expectedMuleArchiverClass,
                        actualMuleArchiverClass);
  }

  @Test
  public void createDeployableFileSettingMuleFolderTest() throws IOException {
    File muleFolderMock = mock(File.class);
    when(muleFolderMock.exists()).thenReturn(true);
    when(muleFolderMock.isDirectory()).thenReturn(true);
    this.packageBuilder.withMule(muleFolderMock);

    MuleArchiver muleArchiverMock = mock(MuleArchiver.class);
    this.packageBuilder.withArchiver(muleArchiverMock);

    this.packageBuilder.withDestinationFile(destinationFileMock);

    this.packageBuilder.createDeployableFile();

    verify(muleArchiverMock, times(1)).addMule(muleFolderMock, null, null);
    verify(muleArchiverMock, times(1)).setDestFile(destinationFileMock);
    verify(muleArchiverMock, times(1)).createArchive();
  }

  @Test
  public void createDeployableFileSettingClassesFolderTest() throws IOException {
    File classesFolderMock = mock(File.class);
    when(classesFolderMock.exists()).thenReturn(true);
    when(classesFolderMock.isDirectory()).thenReturn(true);
    this.packageBuilder.withClasses(classesFolderMock);

    MuleArchiver muleArchiverMock = mock(MuleArchiver.class);
    this.packageBuilder.withArchiver(muleArchiverMock);

    this.packageBuilder.withDestinationFile(destinationFileMock);

    this.packageBuilder.createDeployableFile();

    verify(muleArchiverMock, times(1)).addClasses(classesFolderMock, null, null);
    verify(muleArchiverMock, times(1)).setDestFile(destinationFileMock);
    verify(muleArchiverMock, times(1)).createArchive();
  }

}
