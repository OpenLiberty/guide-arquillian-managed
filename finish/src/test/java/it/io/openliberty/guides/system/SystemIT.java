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

    // tag::system_functional_test[]
    @Deployment(name = "system_functional_test")
    public static JavaArchive createSystemFunctionalTestDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                                        .addClasses(SystemApplication.class,
                                                    SystemResource.class);
        return archive;
    }
    // end::system_functional_test[]

    // tag::system_endpoint_test[]
    @Deployment(name = "system_endpoint_test", testable = false)
    public static WebArchive createSystemEndpointTestDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "arquillian-managed.war")
                                       .addClasses(SystemResource.class,
                                                   SystemApplication.class);
        return archive;
    }
    // end::system_endpoint_test[]

    @Inject
    SystemResource system;

    // tag::testGetPropertiesFromFunction[]
    @Test
    @InSequence(1)
    @OperateOnDeployment("system_functional_test")
    public void testGetPropertiesFromFunction() {
        System.out.println("******************************testGetPropertiesFromFunction*****");
        Properties prop = system.getProperties();
        String expectedOS = System.getProperty("os.name");
        String serviceOS = prop.getProperty("os.name");

        Assert.assertNotNull(serviceOS);
        System.out.println("Test the system property for the service JVM is not null.");

        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        System.out.println("Test the system property for the local and service JVM should match.");
        System.out.println("******************************");
    }
    // end::testGetPropertiesFromFunction[]

    // tag::testGetPropertiesFromEndpoint[]
    @Test
    @InSequence(2)
    @OperateOnDeployment("system_endpoint_test")
    public void testGetPropertiesFromEndpoint(
                    @ArquillianResteasyResource("system") WebTarget webTarget) {
        System.out.println("******************************testGetPropertiesFromEndpoint*****");
        final Response response = webTarget.path("/properties")
                                           .request(MediaType.APPLICATION_JSON).get();
       
        System.out.println("WebTarget URI is: " + webTarget.getUri().toASCIIString());
        
        Assert.assertEquals(deploymentURL + "system",
                            webTarget.getUri().toASCIIString());
        System.out.println("Test the client deployment URI and the endpoint URI should match.");

        Assert.assertEquals(MediaType.APPLICATION_JSON,
                            response.getMediaType().toString());
        System.out.println("Test the endpoint response media type is as expected.");

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        System.out.println("Test the endpoint response status code is OK.");

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();
        Assert.assertEquals("The system property for the local and service JVM should match",
                            System.getProperty("os.name"), jObj.getString("os.name"));
        System.out.println("Test system property for the local and service JVM should match.");
        response.close();
        System.out.println("******************************");
    }
    // end::testGetPropertiesFromEndpoint[]
}
