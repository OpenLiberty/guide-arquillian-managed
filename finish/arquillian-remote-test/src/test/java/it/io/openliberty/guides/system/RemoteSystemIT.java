// tag::copyright[]
/*******************************************************************************
* Copyright (c) 2018 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - Initial implementation
*
********************************************************************************
* Copyright 2012, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.system;

import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import java.io.InputStream;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class RemoteSystemIT {

    @Deployment
    public static WebArchive createDeployment() {
        File[] mavenFiles = Maven.resolver().loadPomFromFile("pom.xml")
                                 .importRuntimeDependencies().resolve()
                                 .withTransitivity().asFile();

        WebArchive archive = ShrinkWrap.create(WebArchive.class)
                                       .addClasses(SystemApplication.class,
                                                   SystemResource.class)
                                       .addAsLibraries(mavenFiles);
        return archive;
    }

    @Test
    public void test_getPropertiesThroughURL() throws Exception {
        URL endpoint = new URL("http://localhost:9080/system/properties");
        System.out.println("endpoint url: " + endpoint);
        String body = readAllAndClose(endpoint.openStream());
        int bodyLength = body.length();
        System.out.println("body length is " + bodyLength);
        assertTrue(bodyLength > 0);

        /*Properties prop = system.getProperties();
        String expectedOS = System.getProperty("os.name");
        System.out.println("Expected OS name: " + expectedOS);
        String serviceOS = prop.getProperty("os.name");
        System.out.println("Service OS name: " + serviceOS);

        Assert.assertNotNull(serviceOS);
        System.out.println("Test the system property for the service JVM is not null.");

        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        System.out.println("Test the system property for the local and service JVM should match.");*/

    }

    String readAllAndClose(InputStream is) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int read;
            while ((read = is.read()) != -1) {
                out.write(read);
            }
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return out.toString();
    }
}