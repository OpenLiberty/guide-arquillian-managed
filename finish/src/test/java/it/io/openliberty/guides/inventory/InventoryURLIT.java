package it.io.openliberty.guides.inventory;

import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @Deployment(name = "inventory_functional_test")
    public static JavaArchive createDeploymentForFunctionalTest() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                                        .addClasses(SystemApplication.class,
                                                    SystemResource.class,
                                                    InventoryList.class,
                                                    SystemData.class,
                                                    SystemClient.class,
                                                    InventoryApplication.class,
                                                    InventoryManager.class,
                                                    InventoryResource.class);
        return archive;
    }
    
    @Deployment(name = "inventory_endpoint_test", testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class,
                                               "arquillian-managed.war")
                                       .addClasses(SystemResource.class,
                                                   SystemApplication.class,
                                                   InventoryList.class,
                                                   SystemData.class, 
                                                   SystemClient.class,
                                                   InventoryApplication.class,
                                                   InventoryManager.class,
                                                   InventoryResource.class);
        return archive;
    }
    
    @Inject
    //InventoryManager invMgr;
    //SystemClient sysClient;
    InventoryResource invSrv; 

    @Test
    @InSequence(1)
    @OperateOnDeployment("inventory_endpoint_test")
    public void testEmptyInventory(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        //System.out.println("Inventory URL test 1 started.");
        final Response response = webTarget.path("/systems")
                                           .request(MediaType.APPLICATION_JSON).get();
        //System.out.println("Client deployment URI is: " + deploymentURL + "inventory");
        //System.out.println("WebTarget URI is: " + webTarget.getUri().toASCIIString());
        Assert.assertEquals(deploymentURL + "inventory",
                            webTarget.getUri().toASCIIString());

        //System.out.println("Client response type is: "
        //                + response.getMediaType().toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON,
                            response.getMediaType().toString());

        //System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        System.out.println("Test the endpoint response status code is OK.");

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        //System.out.println("Client response read entity is: " + obj);
        //Assert.assertTrue(obj.contains("{\"systems\":[],\"total\":0}"));
        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();
        Assert.assertEquals(0, jObj.getInt("total"));
        System.out.println("Test the inventory is empty on application start.");

        response.close();
        System.out.println("******************************");
    }

    @Test
    @InSequence(2)
    @OperateOnDeployment("inventory_endpoint_test")
    public void testHostRegistration(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        //System.out.println("Inventory URL test 2 started.");
        Response response = webTarget.path("/systems/{hostname}")
                                     .resolveTemplate("hostname", "localhost")
                                     .request(MediaType.APPLICATION_JSON).get();

        //System.out.println("WebTarget URI is: " + webTarget.getUri());

        //System.out.println("Client response type is: "
        //                + response.getMediaType().toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON,
                            response.getMediaType().toString());

        //System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        System.out.println("Test the endpoint response status code is OK.");

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        //System.out.println("Client response read entity is: " + obj);
        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();
        Assert.assertEquals("The system property for the local and service JVM should match",
                            System.getProperty("os.name"), jObj.getString("os.name"));
        System.out.println("Test localhost is registered successfully.");
        
        response.close();
        
        System.out.println("******************************");
    }

    @Test
    @InSequence(3)
    @OperateOnDeployment("inventory_endpoint_test")
    public void testSystemPropertiesMatch(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        //System.out.println("Inventory URL test 3 started.");
        Response response = webTarget.path("/systems")
                                     .request(MediaType.APPLICATION_JSON).get();

        //System.out.println("WebTarget URI is: " + webTarget.getUri());

        //System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        System.out.println("Test the endpoint response status code is OK.");

        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        //System.out.println("Client response read entity is: " + obj);
        JsonObject jObj = Json.createReader(new StringReader(obj)).readObject();
        // {"systems":[{"hostname":"localhost","properties":{"user.name":"evelinec","os.name":"Mac
        // OS X"}}],"total":1}

        int expected = 1;
        int actual = jObj.getInt("total");
        Assert.assertEquals("The inventory should have one entry for localhost",
                            expected, actual);
        System.out.println("Test the inventory should have one entry.");

        boolean localhostExists = jObj.getJsonArray("systems").getJsonObject(0)
                                      .get("hostname").toString().contains("localhost");
        Assert.assertTrue("A host was registered, but it was not localhost",
                          localhostExists);
        System.out.println("Test the inventory should have localhost registered.");

        String expectedOS = System.getProperty("os.name");
        JsonObject jProps = (JsonObject) jObj.getJsonArray("systems").getJsonObject(0)
                                             .get("properties");
        String serviceOS = jProps.getString("os.name");
        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        System.out.println("Test system property for the local and service JVM should match.");
        
        response.close();
        System.out.println("******************************");
    }
    
    @Test
    @InSequence(4)
    @OperateOnDeployment("inventory_endpoint_test")
    public void testUnknownHost(
                    @ArquillianResteasyResource("inventory") WebTarget webTarget) {
        System.out.println("******************************");
        //System.out.println("Inventory URL test 4 started.");
        Response response = webTarget.path("/systems/{hostname}")
                                     .resolveTemplate("hostname", "badhostname")
                                     .request(MediaType.APPLICATION_JSON).get();

        //System.out.println("WebTarget URI is: " + webTarget.getUri());

        //System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            response.getStatus());
        System.out.println("Test the endpoint response status code is ERROR.");

        String obj = response.readEntity(String.class);
        //System.out.println("Client response read entity is: " + obj);
        boolean isError = obj.contains("ERROR");
        Assert.assertTrue("badhostname is not a valid host but it didn't raise an error",
                          isError);
        System.out.println("Test the endpoint response message is ERROR.");
        
        response.close();
        System.out.println("******************************");
    }
    
/*    @Test
    @InSequence(5)
    @OperateOnDeployment("inventory_functional_test")
    public void testInventoryManagerFunctions() {
        System.out.println("******************************");
        InventoryList invList = invMgr.list();
        System.out.println("Inv List is: " + invList.getTotal());
        
        Properties props = invMgr.get("localhost");
        System.out.println("Props is: " + props);
        
        invMgr.add("localhost", props);
        
        invList = invMgr.list();
        System.out.println("Inv List is: " + invList.getTotal());
        System.out.println("Inv List is: " + invList);
        
        List<SystemData> systemDataList = invList.getSystems();
        System.out.println("System data list is: " + systemDataList);
        
        System.out.println("systemDataList.get(0): " + systemDataList.get(0).getHostname());
        System.out.println("systemDataList.get(0).getProperties().get(\"os.name\"): " + systemDataList.get(0).getProperties().get("os.name"));
        
        Assert.assertTrue(systemDataList.get(0).getHostname().equals("localhost"));
        Assert.assertTrue(systemDataList.get(0).getProperties().get("os.name").equals(System.getProperty("os.name")));
        
        // {"systems":[{"hostname":"localhost","properties":{"user.name":"evelinec","os.name":"Mac
        // OS X"}}],"total":1}

        System.out.println("******************************");
    }*/
    
/*    @Test
    @InSequence(6)
    @OperateOnDeployment("inventory_functional_test")
    public void testSystemClientFunction() {
        System.out.println("******************************SystemClient");
        Properties props = sysClient.getProperties("localhost");
        System.out.println("System client props are: " + props);
        String expectedOS = System.getProperty("os.name");
        System.out.println("Expected OS name: " + expectedOS);
        String serviceOS = props.getProperty("os.name");
        System.out.println("Service OS name: " + serviceOS);

        Assert.assertNotNull(serviceOS);
        System.out.println("Test the system property for the service JVM is not null.");

        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        System.out.println("Test the system property for the local and service JVM should match.");
        System.out.println("******************************");
    }*/
    
    @Test
    @InSequence(7)
    @OperateOnDeployment("inventory_functional_test")
    public void testInventoryResourceFunctions() {
        System.out.println("******************************Test InventoryResource Functions*****");
        invSrv.getPropertiesForHost("localhost");
        
        InventoryList invList = invSrv.listContents();
        Assert.assertEquals(1, invList.getTotal());
        System.out.println("Test the inventory should have one entry.");
        
        //System.out.println("Inv List total is: " + invList.getTotal());
        
        List<SystemData> systemDataList = invList.getSystems();
        //System.out.println("System data list is: " + systemDataList);
        
        //System.out.println("systemDataList.get(0): " + systemDataList.get(0).getHostname());
        //System.out.println("systemDataList.get(0).getProperties().get(\"os.name\"): " + systemDataList.get(0).getProperties().get("os.name"));
        
        Assert.assertTrue(systemDataList.get(0).getHostname().equals("localhost"));
        System.out.println("Test the inventory should have localhost registered.");
        
        Assert.assertTrue(systemDataList.get(0).getProperties().get("os.name").equals(System.getProperty("os.name")));
        System.out.println("Test system property for the local and service JVM should match.");

        System.out.println("******************************");
    }
}
