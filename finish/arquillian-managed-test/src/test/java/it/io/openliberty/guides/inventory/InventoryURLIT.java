package it.io.openliberty.guides.inventory;

import java.io.StringReader;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
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
public class InventoryURLIT {

    @ArquillianResource
    private URL deploymentURL;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class,
                                               "arquillian-managed.war")
                                       .addClasses(SystemResource.class,
                                                   SystemApplication.class,
                                                   InventoryList.class,
                                                   SystemData.class, SystemClient.class,
                                                   InventoryApplication.class,
                                                   InventoryManager.class,
                                                   InventoryResource.class);
        return archive;
    }

    @Test
    @InSequence(1)
    public void testEmptyInventory(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        System.out.println("Inventory URL test 1 started.");
        final Response response = webTarget.path("/systems")
                                           .request(MediaType.APPLICATION_JSON).get();
        System.out.println("Client deployment URI is: " + deploymentURL + "inventory");
        System.out.println("WebTarget URI is: " + webTarget.getUri().toASCIIString());
        Assert.assertEquals(deploymentURL + "inventory",
                            webTarget.getUri().toASCIIString());

        System.out.println("Client response type is: "
                        + response.getMediaType().toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON,
                            response.getMediaType().toString());

        System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        System.out.println("Client response read entity is: " + obj);
        Assert.assertTrue(obj.contains("{\"systems\":[],\"total\":0}"));

        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();
        Assert.assertEquals(0, jObj.getInt("total"));

        response.close();
        System.out.println("******************************");
    }

    @Test
    @InSequence(2)
    public void testHostRegistration(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        System.out.println("Inventory URL test 2 started.");
        Response response = webTarget.path("/systems/{hostname}")
                                     .resolveTemplate("hostname", "localhost")
                                     .request(MediaType.APPLICATION_JSON).get();

        System.out.println("WebTarget URI is: " + webTarget.getUri());

        System.out.println("Client response type is: "
                        + response.getMediaType().toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON,
                            response.getMediaType().toString());

        System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        System.out.println("Client response read entity is: " + obj);

        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();

        Assert.assertEquals("The system property for the local and service JVM should match",
                            System.getProperty("os.name"), jObj.getString("os.name"));
        response.close();
        System.out.println("******************************");
    }

    @Test
    @InSequence(3)
    public void testSystemPropertiesMatch(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        System.out.println("Inventory URL test 3 started.");
        Response response = webTarget.path("/systems")
                                     .request(MediaType.APPLICATION_JSON).get();

        System.out.println("WebTarget URI is: " + webTarget.getUri());

        System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        System.out.println("Client response read entity is: " + obj);
        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();
        // {"systems":[{"hostname":"localhost","properties":{"user.name":"evelinec","os.name":"Mac
        // OS X"}}],"total":1}

        int expected = 1;
        int actual = jObj.getInt("total");
        Assert.assertEquals("The inventory should have one entry for localhost",
                            expected, actual);

        boolean localhostExists = jObj.getJsonArray("systems").getJsonObject(0)
                                      .get("hostname").toString().contains("localhost");
        Assert.assertTrue("A host was registered, but it was not localhost",
                          localhostExists);

        String expectedOS = System.getProperty("os.name");
        JsonObject jProps = (JsonObject) jObj.getJsonArray("systems").getJsonObject(0).get("properties");
        String serviceOS = jProps.getString("os.name");
        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        response.close();
        System.out.println("******************************");
    }

    @Test
    @InSequence(4)
    public void testUnknownHost(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        System.out.println("Inventory URL test 4 started.");
        Response response = webTarget.path("/systems/{hostname}")
                                     .resolveTemplate("hostname", "badhostname")
                                     .request(MediaType.APPLICATION_JSON).get();

        System.out.println("WebTarget URI is: " + webTarget.getUri());

        System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            response.getStatus());

        String obj = response.readEntity(String.class);
        System.out.println("Client response read entity is: " + obj);
        boolean isError = obj.contains("ERROR");
        Assert.assertTrue("badhostname is not a valid host but it didn't raise an error",
                          isError);
        response.close();
        System.out.println("******************************");
    }
}
