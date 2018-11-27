package it.io.openliberty.guides.inventory;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
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
import javax.ws.rs.core.Response;

@RunWith(Arquillian.class)
public class InventoryIT {
    
    private Properties prop; 
    
    @ArquillianResource
    private URL deploymentURL;

    @Deployment
    public static JavaArchive createDeployment() {
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

    @Inject
    //InventoryManager invMgr; 
    InventoryResource invSrv;
    
    @Test
    @InSequence(1)
    public void test_InvSrv_getPropertiesForHost() {
        //Response response = invSrv.getPropertiesForHost("localhost");
    }
    
    /*@Test
    @InSequence(1)
    public void test_InvMgr_get() {
        prop = invMgr.get("localhost");
        String expectedOS = System.getProperty("os.name");
        System.out.println("Expected OS name: " + expectedOS);
        String serviceOS = prop.getProperty("os.name");
        System.out.println("Service OS name: " + serviceOS);

        Assert.assertNotNull(serviceOS);
        System.out.println("Test the system property for the service JVM is not null.");

        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        System.out.println("Test the system property for the local and service JVM should match.");
    }*/
    
    /*@Test
    @InSequence(2)
    public void test_InvMgr_add_and_list() {
        invMgr.add("localhost", prop);
        
        InventoryList list = invMgr.list();
        List<SystemData> data = list.getSystems();
        
        Assert.assertEquals("localhost", data.get(0).getHostname());
        
        String expectedOS = System.getProperty("os.name");
        System.out.println("Expected OS name: " + expectedOS);
        
        String serviceOS = data.get(0).getProperties().getProperty("os.name");
        System.out.println("Service OS name: " + serviceOS);

        Assert.assertNotNull(serviceOS);
        System.out.println("Test the system property for the service JVM is not null.");

        Assert.assertEquals("The system property for the local and service JVM should match",
                            expectedOS, serviceOS);
        System.out.println("Test the system property for the local and service JVM should match.");
        
        //Testing InventoryList.getTotal
        Assert.assertEquals(1, list.getTotal()); 
    }*/
}