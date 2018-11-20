// tag::copyright[]
/*******************************************************************************
* Copyright (c) 2018 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - Initial implementation
*
********************************************************************************
* Copyright 2012, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.system;

import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openliberty.guides.system.SystemApplication;
import io.openliberty.guides.system.SystemResource;

@RunWith(Arquillian.class)
public class SystemIT {
    
    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                                        .addClasses(SystemApplication.class, SystemResource.class);
        return archive;
    }
    
    @Inject
    SystemResource system;
    
    @Test
    public void test_getProperties() {
        Properties prop = system.getProperties();
        String expectedOS = System.getProperty("os.name");
        System.out.println("Expected OS name: " + expectedOS);
        String serviceOS = prop.getProperty("os.name");
        System.out.println("Service OS name: " + serviceOS);
        
        Assert.assertNotNull(serviceOS);
        System.out.println("Test the system property for the service JVM is not null.");
        
        Assert.assertEquals("The system property for the local and service JVM should match",
                     expectedOS, 
                     serviceOS);
        System.out.println("Test the system property for the local and service JVM should match.");
        
        
    }
}