// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.model.SystemData;

// tag::ApplicationScoped[]
@ApplicationScoped
// end::ApplicationScoped[]
public class InventoryManager {

    @Inject
    private SystemClient systemClient;

    private List<SystemData> systems = Collections.synchronizedList(new ArrayList<SystemData>());

    public Properties get(String hostname) {
        System.out.println("InvetoryManager hostname: " + hostname);
        System.out.println("InvetoryManager systemClient: " + systemClient);
        return systemClient.getProperties(hostname);
    }
    
    public Properties get(String hostname, String warName) {
        System.out.println("InvetoryManager hostname: " + hostname);
        System.out.println("InvetoryManager warName: " + warName);
        System.out.println("InvetoryManager systemClient: " + systemClient);
        return systemClient.getProperties(hostname, warName);
    }

    public void add(String hostname, Properties systemProps) {
        Properties props = new Properties();
        props.setProperty("os.name", systemProps.getProperty("os.name"));
        props.setProperty("user.name", systemProps.getProperty("user.name"));

        SystemData system = new SystemData(hostname, props);
        if (!systems.contains(system)) {
            systems.add(system);
        }
    }

    public InventoryList list() {
        return new InventoryList(systems);
    }
}