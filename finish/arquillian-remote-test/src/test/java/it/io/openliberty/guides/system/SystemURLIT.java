package it.io.openliberty.guides.system;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemURLIT {
 
    private static final String RESOURCE_PREFIX = SystemApplication.class.getAnnotation(ApplicationPath.class).value().substring(0);
    SystemResource proxy = null;
    
    @ArquillianResource
    private URL deploymentURL;
    
    /*@Deployment(testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class)
                        .addClasses(SystemResource.class, SystemApplication.class) 
                        //.addPackages(true, Filters.exclude(".*IT.*"),
                        //             SystemResource.class.getPackage(), SystemApplication.class.getPackage())
                        .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return archive;
    }*/
    
    public static EnterpriseArchive createDeployment() {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                        .addAsModule(ShrinkWrap.create(WebArchive.class, "test.war")
                        .addClasses(SystemResource.class, SystemApplication.class)); 
                        //.addPackages(true, Filters.exclude(".*IT.*"),
                        //             SystemResource.class.getPackage(), SystemApplication.class.getPackage())
        System.out.println(ear.toString(true));
        return ear;
    }
    
    
    
   /* @Test
    //@RunAsClient
    public void test_getProperties(@ArquillianResteasyResource("systems") ResteasyWebTarget webTarget) {
        final Response response = webTarget
                        .path("/properties")
                        .request(MediaType.APPLICATION_JSON)
                        .get();
        System.out.println(deploymentURL + "systems");
        Assert.assertEquals(deploymentURL + "systems", webTarget.getUri().toASCIIString());
        System.out.println("Test web target URI.");
        
        System.out.println(response);
        Assert.assertNotNull(response);
        System.out.println("Test response is not null.");
        //Assert.assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        //Assert.assertEquals(true, response.readEntity(Boolean.class));
    }*/
    
    /*@Test
    //@RunAsClient
    public void test_getProperties(@ArquillianResteasyResource WebTarget webTarget) {
        System.out.println("System URL test started.");
        final Response response = webTarget
                        .path("/properties")
                        .request(MediaType.APPLICATION_JSON)
                        .get();
        //System.out.println(deploymentURL + "systems");
        //Assert.assertEquals(deploymentURL + "systems", webTarget.getUri().toASCIIString());
        //Assert.assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
        
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        //Assert.assertEquals(true, response.readEntity(Boolean.class));
    }*/
    
/*    @BeforeClass
    public static void initResteasyClient() {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }*/
    
    @Before
    public void beforeClass() throws MalformedURLException {
        deploymentURL = new URL("http://localhost:9080/");
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(deploymentURL.toString() + RESOURCE_PREFIX + "/properties"));
        proxy = target.proxy(SystemResource.class);
        System.out.println(target.getUri());
    }
    
    @Test
    @RunAsClient
    public void testGetCustomerByIdUsingClientRequest() throws Exception {
        
        //deploymentURL = new URL("http://localhost:9080/");
        System.out.println(deploymentURL.toString() + RESOURCE_PREFIX + "/properties");
        //SystemResource client = ProxyFactory.create(SystemResource.class, deploymentURL.toString() + RESOURCE_PREFIX + "/properties");
        //System.out.println(client);
        //Properties prop = client.getProperties();
        
        /*ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(deploymentURL.toString() + RESOURCE_PREFIX + "/properties"));
        SystemResource proxy = target.proxy(SystemResource.class);*/
        
        System.out.println(proxy);
        Properties prop = proxy.getProperties();
        
        Assert.assertNotNull(prop);
        System.out.println(prop);
        
        
        //deploymentUrl = new URL("http://localhost:8180/test/");
        // GET http://localhost:8080/test/rest/customer/1
        //System.out.println(deploymentURL.toString() + RESOURCE_PREFIX + "/properties");
        //ClientRequest request = new ClientRequest(deploymentURL.toString() + RESOURCE_PREFIX + "/properties");

        // we're expecting a String back
        //ClientResponse<String> responseObj = request.get(String.class);
        //ClientResponse responseObj = request.get();

        //Assert.assertEquals(200, responseObj.getStatus());
        //System.out.println("GET /customer/1 HTTP/1.1\n\n" + responseObj.getEntity());

        //String response = responseObj.getEntity().replaceAll("<\\?xml.*\\?>", "").trim();
        //Assert.assertEquals("<customer><id>1</id><name>Acme Corporation</name></customer>", response);
    }
    
}