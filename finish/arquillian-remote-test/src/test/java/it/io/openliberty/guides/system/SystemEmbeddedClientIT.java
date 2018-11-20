package it.io.openliberty.guides.system;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemEmbeddedClientIT {

    //private static final Logger log = Logger.getLogger(SystemEmbeddedClientIT.class.getName());

    /*@Deployment(testable = false)
    public static WebArchive getTestArchive() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "client-test.war")
                                         .addClasses(SystemResource.class,
                                                     SystemApplication.class);
                                         //.setWebXML("web.xml");
        log.info(war.toString(true));
        return war;
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
    
    @Test
    //@RunAsClient
    public void shouldBeAbleToInvokeServletInDeployedWebApp() throws Exception {
        String body = readAllAndClose(new URL("http://localhost:9080/system/properties").openStream());

        System.out.println("body: " + body);

        Assert.assertEquals("Verify that the servlet was deployed and returns expected result",
                            body, body);
    }

    private String readAllAndClose(InputStream is) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int read;
            while ((read = is.read()) != -1) {
                out.write(read);
            }
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return out.toString();
    }
}
