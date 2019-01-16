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
package it.io.openliberty.guides.inventory;

import java.util.List;

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
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.inventory.InventoryApplication;
import io.openliberty.guides.inventory.InventoryManager;
import io.openliberty.guides.inventory.InventoryResource;
import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.model.SystemData;
import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class InventoryIT {

    private final static String WARNAME = "arquillian-managed";
    private static String port = System.getProperty("liberty.test.port");
    private static String baseUrl = "http://localhost:" + port + "/";
    private final String INVENTORY_SYSTEMS = "inventory/systems";
    private Client client = ClientBuilder.newClient();

    // tag::deployment[]
    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WARNAME + ".war")
                                       .addClasses(SystemResource.class,
                                                   SystemApplication.class,
                                                   InventoryList.class,
                                                   SystemData.class, SystemClient.class,
                                                   InventoryApplication.class,
                                                   InventoryManager.class,
                                                   InventoryResource.class);
        return archive;
    }
    // end::deployment[]

    @Inject
    InventoryResource invSrv;

    @Test
    @RunAsClient
    @InSequence(1)
    public void testGetPropertiesFromEndpoint() throws Exception {
        String localhosturl = baseUrl + WARNAME + "/" + INVENTORY_SYSTEMS
                        + "/localhost";

        client.register(JsrJsonpProvider.class);
        WebTarget localhosttarget = client.target(localhosturl);
        Response localhostresponse = localhosttarget.request().get();

        Assert.assertEquals("Incorrect response code from " + localhosturl, 200,
                            localhostresponse.getStatus());

        JsonObject localhostobj = localhostresponse.readEntity(JsonObject.class);
        Assert.assertEquals("The system property for the local and remote JVM should match",
                            System.getProperty("os.name"),
                            localhostobj.getString("os.name"));

        String invsystemsurl = baseUrl + WARNAME + "/" + INVENTORY_SYSTEMS;

        WebTarget invsystemstarget = client.target(invsystemsurl);
        Response invsystemsresponse = invsystemstarget.request().get();

        Assert.assertEquals("Incorrect response code from " + localhosturl, 200,
                            invsystemsresponse.getStatus());

        JsonObject invsystemsobj = invsystemsresponse.readEntity(JsonObject.class);

        int expected = 1;
        int actual = invsystemsobj.getInt("total");
        Assert.assertEquals("The inventory should have one entry for localhost",
                            expected, actual);
        localhostresponse.close();
    }

    // tag::testInventoryResourceFunctions[]
    @Test
    @InSequence(2)
    public void testInventoryResourceFunctions() {

        // Listing the inventory contents that were stored in the previous test
        // case
        InventoryList invList = invSrv.listContents();
        Assert.assertEquals(1, invList.getTotal());

        List<SystemData> systemDataList = invList.getSystems();
        Assert.assertTrue(systemDataList.get(0).getHostname().equals("localhost"));

        Assert.assertTrue(systemDataList.get(0).getProperties().get("os.name")
                                        .equals(System.getProperty("os.name")));
    }
    // end::testInventoryResourceFunctions[]
}
