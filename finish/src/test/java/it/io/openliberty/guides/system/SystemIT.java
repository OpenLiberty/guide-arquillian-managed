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

import java.util.Properties;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemIT {

    private final static String WARNAME = "arquillian-managed";

    // tag::deployment[]
    @Deployment(testable = true)
    public static WebArchive createSystemEndpointTestDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WARNAME + ".war")
                                       .addClasses(SystemResource.class,
                                                   SystemApplication.class);
        return archive;
    }
    // end::deployment[]

    @Inject
    SystemResource system;

    @Test
    public void testGetPropertiesFromFunction() throws Exception {
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
    
    // tag::testGetPropertiesFromEndpoint[]
    @Test
    @RunAsClient
    public void testGetPropertiesFromEndpoint() throws Exception {
        System.out.println("******************************testGetPropertiesFromEndpoint*****");
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/";
        System.out.println("Endpoint URL: " + url);
        Client client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);

        WebTarget target = client.target(url + WARNAME + "/system/properties");
        Response response = target.request().get();

        Assert.assertEquals("Incorrect response code from " + url, 200,
                            response.getStatus());
        System.out.println("Test the endpoint response status code is OK.");

        JsonObject obj = response.readEntity(JsonObject.class);
        Assert.assertEquals("The system property for the local and remote JVM should match",
                            System.getProperty("os.name"), obj.getString("os.name"));
        System.out.println("Test the system property for the local and service JVM should match.");
        response.close();
        System.out.println("******************************");
    }
    // end::testGetPropertiesFromEndpoint[]
}
