// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019, 2022 IBM Corporation and others.
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

import java.net.URL;
import java.util.Properties;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemArquillianIT {

    private final static String WARNAME = System.getProperty("arquillian.war.name");

    @Deployment(testable = true)
    public static WebArchive createSystemEndpointTestDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WARNAME)
                                       .addPackages(true, "io.openliberty.guides.system");
        return archive;
    }

    @ArquillianResource
    private URL baseURL;

    @Inject
    SystemResource system;

    @Test
    public void testGetPropertiesFromFunction() throws Exception {
        Properties prop = system.getProperties();
        String expectedOS = System.getProperty("os.name");
        String serviceOS = prop.getProperty("os.name");

        Assert.assertNotNull(serviceOS);
        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
    }

    @Test
    @RunAsClient
    public void testGetPropertiesFromEndpoint() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target(baseURL + "/system/properties");
        Response response = target.request().get();

        Assert.assertEquals("Incorrect response code from " + baseURL, 200,
                            response.getStatus());

        JsonObject obj = response.readEntity(JsonObject.class);
        Assert.assertEquals("The system property for the local and remote JVM should match",
                            System.getProperty("os.name"), obj.getString("os.name"));
        response.close();
    }
}
