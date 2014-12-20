package com.cdi.crud.test;

import com.cdi.crud.bean.CrudBean;
import com.cdi.crud.rest.CarEndpoint;
import com.cdi.crud.test.dbunit.DBUnitRest;
import com.cdi.crud.test.dbunit.DBUnitUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.dbunit.dataset.Row;
import org.jboss.arquillian.persistence.dbunit.dataset.Table;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSet;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSetProducer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.net.URL;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Created by rmpestano on 12/20/14.
 */
@RunWith(Arquillian.class)
public class CrudRest {

    @Deployment(name = "cdi-rest.war", testable = false)//run as client
    public static Archive<?> createDeployment() {
        WebArchive war = Deployments.getBaseDeployment();
        war.addPackages(true, CarEndpoint.class.getPackage());

                //create dbunit dataset remotely
        war.addAsResource("datasets/car.yml","car.yml").//needed by DBUnitUtils
        addClass(DBUnitUtils.class).addClass(DBUnitRest.class).addClass(CrudBean.class).addClass(YamlDataSet.class).
                addClass(YamlDataSetProducer.class).
                addClass(Row.class).addClass(Table.class).addClass(DBUnitRest.class);
        MavenResolverSystem resolver = Maven.resolver();
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.dbunit:dbunit:2.5.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.yaml:snakeyaml:1.10").withoutTransitivity().asSingleFile());
        System.out.println(war.toString(true));
        return war;
    }

    @ArquillianResource
    URL basePath;

    @Before
    public void initDataset() {
        DBUnitUtils.createRemoteDataset(basePath, "car.yml");
    }

    @After
    public void clear() {
        DBUnitUtils.deleteRemoteDataset(basePath, "car.yml");
    }

    @Test
    public void shouldListCars() {
        given().
                queryParam("start",0).queryParam("max",10).
        when().
                get(basePath + "rest/cars/list").
        then().
                statusCode(Response.Status.OK.getStatusCode()).
                body("", hasSize(4)).//dataset has 4 cars
                body("model", hasItem("Ferrari")).
                body("price", hasItem(2450.8f)).
                body(containsString("Porche274"));
    }

}
