package com.cdi.crud.it;

import com.cdi.crud.deployment.EnableFileDeployment;
import com.cdi.crud.deployment.EnableMavenBuildDeployment;
import com.cdi.crud.service.CarService;
import org.jboss.arquillian.container.test.api.BeforeDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pestano on 11/11/15.
 */
@RunWith(Arquillian.class)
@EnableMavenBuildDeployment
public class SimplestDeployment {


    @BeforeDeployment
    public static Archive beforeDeployment(Archive archive) {
        WebArchive war = (WebArchive) archive;
        war.addAsResource("persistence.xml", "META-INF/persistence.xml");//replace with test persistence
        return war;
    }

    @Inject
    CarService carService;


    @Test
    @UsingDataSet("car.yml")
    public void shouldCountCars() {
        assertNotNull(carService);
        assertEquals(carService.crud().count(), 4);
    }
}
