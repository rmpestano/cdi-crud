package com.cdi.crud.person.test;

import com.cdi.crud.person.service.PersonService;
import org.arquillian.cube.CubeController;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by rmpestano on 12/01/15.
 */
@RunWith(Arquillian.class)
public class PersonIt {

    @ArquillianResource
    private CubeController cubeController;

    @Inject
    PersonService personService;

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive war = Deployments.getBaseDeployment();
        System.out.println(war.toString(true));
        return war;
    }

    @Before
    public void tearUp(){
        cubeController.create("car_service");
        try{
            cubeController.start("car_service");
        }catch (Exception e){
            e.printStackTrace();
        }
        System.setProperty("carservice.endpoint.url","http://localhost:8180/car-service/rest");
    }

    @After
    public void tearDown(){
        cubeController.stop("car_service");
        cubeController.destroy("car_service");

    }

    @Test
    @InSequence(1)
    public void shouldBeTrue(){
        assertTrue(true);
    }

    @Test
    @InSequence(2)
    public void carServiceShouldBeAccessible(){
        assertEquals(personService.getCarServiceStatus(),200);
    }

}
