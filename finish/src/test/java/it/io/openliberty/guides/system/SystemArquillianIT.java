// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
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
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemArquillianIT {

    private final static String WARNAME = "arquillian-managed.war";

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
        assertEquals(expectedOS, serviceOS, "The system property for the local and service JVM should match");
    }

    @Test
    @RunAsClient
    public void testGetPropertiesFromEndpoint() throws Exception {
        Client client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);

        WebTarget target = client.target(baseURL + "/system/properties");
        Response response = target.request().get();

        assertEquals(200, response.getStatus(),"Incorrect response code from " + baseURL);

        JsonObject obj = response.readEntity(JsonObject.class);
        assertEquals(System.getProperty("os.name"), obj.getString("os.name"), "The system property for the local and remote JVM should match");
        response.close();
    }
}
