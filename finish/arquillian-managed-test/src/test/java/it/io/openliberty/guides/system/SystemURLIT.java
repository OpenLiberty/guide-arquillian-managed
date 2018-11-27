package src.test.java.it.io.openliberty.guides.system;

import java.net.URL;

import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemURLIT {
    
    @ArquillianResource
    private URL deploymentURL;
    
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class)
                        .addClasses(SystemResource.class, SystemApplication.class, JsonObject.class); 
        return archive;
    }
    
    @Test
    public void test_getProperties(@ArquillianResteasyResource("system") WebTarget webTarget) {
        System.out.println("System URL test started.");
        final Response response = webTarget
                        .path("/properties")
                        .request(MediaType.APPLICATION_JSON)
                        .get();
        System.out.println("Client deployment URI is: " + deploymentURL + "system");
        System.out.println("WebTarget URI is: " + webTarget.getUri().toASCIIString());
        Assert.assertEquals(deploymentURL + "system", webTarget.getUri().toASCIIString());
        
        System.out.println("Client response type is: " + response.getMediaType().toString());
        Assert.assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
        
        System.out.println("Client response is: " + response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        /*System.out.println("Client response read entity boolean is: " + response.readEntity(JsonObject.class));
        JsonObject obj = response.readEntity(JsonObject.class);*/
        
        String obj = response.readEntity(MediaType.APPLICATION_JSON.getClass());
        System.out.println("Client response read entity is: " + obj);
        Assert.assertTrue(obj.contains("\"os.name\":\"Mac OS X\""));
        /*assertEquals("The system property for the local and service JVM should match",
                     System.getProperty("os.name"), obj.getString("os.name"));*/
        response.close();
    }
}