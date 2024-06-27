// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import io.openliberty.guides.system.SystemResource;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SystemArquillianIT {

    private static final String WARNAME = System.getProperty("arquillian.war.name");

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

        assertNotNull(serviceOS);
        assertEquals(expectedOS, serviceOS,
                "The system property for the local"
                        + " and service JVM should match");
    }

    @Test
    @RunAsClient
    public void testGetPropertiesFromEndpoint() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target(baseURL + "/system/properties");
        Response response = target.request().get();

        assertEquals(200,
                            response.getStatus(),"Incorrect response code from " + baseURL);

        JsonObject obj = response.readEntity(JsonObject.class);
        assertEquals(System.getProperty("os.name"), obj.getString("os.name"),
                "The system property for the local"
                        + " and remote JVM should match");
        response.close();
    }
}
