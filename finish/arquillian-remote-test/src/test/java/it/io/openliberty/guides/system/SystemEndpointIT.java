//tag::comment[]
/*******************************************************************************
* Copyright (c) 2017 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
// end::comment[]
package it.io.openliberty.guides.system;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import javax.json.JsonObject;
import javax.ws.rs.ApplicationPath;
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
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemEndpointIT {
    
    private static final String RESOURCE_PREFIX = SystemApplication.class.getAnnotation(ApplicationPath.class).value().substring(0);
    SystemResource proxy = null;
    
    @ArquillianResource
    private URL deploymentURL;
    
    
    @Deployment//(testable = false)
    public static EnterpriseArchive createDeployment() {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                        .addAsModule(ShrinkWrap.create(WebArchive.class, "test.war")
                        .addClasses(SystemResource.class, SystemApplication.class)); 
                        //.addPackages(true, Filters.exclude(".*IT.*"),
                        //             SystemResource.class.getPackage(), SystemApplication.class.getPackage())
        return ear;
    }
    
    @Test
    @RunAsClient
    public void testGetProperties() throws MalformedURLException {
        //String port = System.getProperty("liberty.test.port");
        //String url = "http://localhost:" + port + "/";
        
        deploymentURL = new URL("http://localhost:9080/");
        
        Client client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);

        //WebTarget target = client.target(url + "system/properties");
        
        System.out.println("URL: " + deploymentURL.toString() + RESOURCE_PREFIX + "/properties");
        WebTarget target = client.target(deploymentURL.toString() + RESOURCE_PREFIX + "/properties");
        
        Response response = target.request().get();
        System.out.println(response);
        System.out.println("Target URI: " + target.getUri());
        System.out.println(response.getStatus());
        System.out.println(response.getMediaType());
        System.out.println(response.getEntity());
        
        //assertEquals("Incorrect response code from " + url, 200, response.getStatus());

        JsonObject obj = response.readEntity(JsonObject.class);

        assertEquals("The system property for the local and remote JVM should match",
                     System.getProperty("os.name"), obj.getString("os.name"));

        response.close();
    }
}