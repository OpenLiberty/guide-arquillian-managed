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

import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemIT {

    @ArquillianResource
    private URL deploymentURL;

    @Deployment(name = "system_functional_test")
    public static JavaArchive createSystemFunctionalTestDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                                        .addClasses(SystemApplication.class,
                                                    SystemResource.class);
        return archive;
    }

    @Deployment(name = "system_endpoint_test", testable = false)
    public static WebArchive createSystemEndpointTestDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class)
                                       .addClasses(SystemResource.class,
                                                   SystemApplication.class,
                                                   JsonObject.class,
                                                   MessageBodyReader.class);
        return archive;
    }

    @Inject
    SystemResource system;

    @Test
    @InSequence(1)
    @OperateOnDeployment("system_functional_test")
    public void testGetPropertiesFromFunction() {
        System.out.println("******************************");
        Properties prop = system.getProperties();
        String expectedOS = System.getProperty("os.name");
        //System.out.println("Expected OS name: " + expectedOS);
        String serviceOS = prop.getProperty("os.name");
        //System.out.println("Service OS name: " + serviceOS);

        Assert.assertNotNull(serviceOS);
        System.out.println("Test the system property for the service JVM is not null.");

        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        System.out.println("Test the system property for the local and service JVM should match.");
        System.out.println("******************************");
    }

    @Test
    @InSequence(2)
    @OperateOnDeployment("system_endpoint_test")
    public void testGetPropertiesFromWeb(
                    @ArquillianResteasyResource("system") WebTarget webTarget) {
        System.out.println("******************************");
        //System.out.println("System URL test started.");
        final Response response = webTarget.path("/properties")
                                           .request(MediaType.APPLICATION_JSON).get();
        //System.out.println("Client deployment URI is: " + deploymentURL + "system");
        //System.out.println("WebTarget URI is: " + webTarget.getUri().toASCIIString());
        Assert.assertEquals(deploymentURL + "system",
                            webTarget.getUri().toASCIIString());
        System.out.println("Test the client deployment URI and the endpoint URI should match.");

        //System.out.println("Client response type is: "
        //                + response.getMediaType().toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON,
                            response.getMediaType().toString());
        System.out.println("Test the endpoint response media type is as expected.");

        //System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        System.out.println("Test the endpoint response status code is OK.");

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        //System.out.println("Client response read entity is: " + obj);
        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();
        Assert.assertEquals("The system property for the local and service JVM should match",
                            System.getProperty("os.name"), jObj.getString("os.name"));
        System.out.println("Test system property for the local and service JVM should match.");
        response.close();
        System.out.println("******************************");
    }
}