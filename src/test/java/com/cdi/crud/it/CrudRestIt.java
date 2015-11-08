package com.cdi.crud.it;

import com.cdi.crud.util.Deployments;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response.Status;
import java.net.URL;
import java.text.ParseException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rmpestano on 12/20/14.
 */
@RunWith(Arquillian.class)
public class CrudRestIt {

    @Deployment(name = "cdi-rest.war")
    public static Archive<?> createDeployment() {
        WebArchive war = Deployments.getBaseDeployment();
        MavenResolverSystem resolver = Maven.resolver();
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.jayway.restassured:rest-assured").withTransitivity().asFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.google.code.gson:gson:2.4").withoutTransitivity().asSingleFile());
        System.out.println(war.toString(true));
        return war;
    }

    @ArquillianResource
    URL basePath;



    @Test
    @UsingDataSet("car.yml")
    public void shouldListCars() {
        given().
                queryParam("start", 0).queryParam("max", 10).
        when().
                get(basePath + "rest/cars").
        then().
                statusCode(Status.OK.getStatusCode()).
                body("", hasSize(4)).//dataset has 4 cars
                body("model", hasItem("Ferrari")).
                body("price", hasItem(2450.8f)).
                body(containsString("Porche274"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldListCarsByPrice() {
        given().
                queryParam("minPrice", 2450f).queryParam("maxPrice", 12999).
                when().
                get(basePath + "rest/cars").
                then().
                statusCode(Status.OK.getStatusCode()).
                body("", hasSize(2)).
                body("model", hasItem("Ferrari")).
                body("model", hasItem("Mustang")).
                body("price", hasItem(2450.8f)).
                body("model", not(hasItem("Porche")));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldListCarsByModel() {
        given().
                queryParam("model", "Porche").
                when().
                get(basePath + "rest/cars").
                then().
                statusCode(Status.OK.getStatusCode()).
                body("", hasSize(2)).
                body("model", hasItem("Porche")).
                body("model", hasItem("Porche274")).
                body("price", hasItem(18990.23f)).
                body("model", not(hasItem("Ferrari")));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldListCarsByName() {
        given().
                queryParam("name", "spider").
                when().
                get(basePath + "rest/cars").
                then().
                statusCode(Status.OK.getStatusCode()).
                body("", hasSize(2)).
                body("model", hasItem("Mustang")).
                body("name", hasItem("mustang spider")).
                body("price", hasItem(12999.0f)).
                body("name", hasItem("ferrari spider")).
                body("price", hasItem(2450.8f)).
                body("model", not(hasItem("Porche")));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldCountCars() {
        given().
                when().
                get(basePath + "rest/cars/count").
                then().
                statusCode(Status.OK.getStatusCode()).
                body(equalTo("4"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldFindCar() {
        String json =
                given().
                        when().
                        get(basePath + "rest/cars/1").  //dataset has car with id =1
                        then().
                        statusCode(Status.OK.getStatusCode()).
                        body("id", equalTo(1)).
                        body("model", equalTo("Ferrari")).
                        body("price", equalTo(2450.8f)).extract().asString();

        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        assertEquals("Ferrari", jsonObject.get("model").getAsString());

    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldFindCarUsingCache() throws InterruptedException, ParseException {
        Response response =
                given().
                        when().
                        get(basePath + "rest/cars/1").  //dataset has car with id =1
                        then().
                        statusCode(Status.OK.getStatusCode()).
                        body("id", equalTo(1)).
                        body("model", equalTo("Ferrari")).
                        body("price", equalTo(2450.8f)).extract().response();

        String etag = response.getHeader("etag");
        assertNotNull("etag");
        given().
                header("If-None-Match", etag).
                when().
                get(basePath + "rest/cars/1").  //dataset has car with id =1
                then().
                statusCode(Status.NOT_MODIFIED.getStatusCode());


    }


    @Test
    public void shouldCreateCar() {
        JsonObject carToCreate = new JsonObject();
        carToCreate.add("model", new JsonPrimitive("new car"));
        carToCreate.add("name", new JsonPrimitive("new car name"));
        carToCreate.add("price", new JsonPrimitive(1000f));
        String result = given().
                content(carToCreate.toString()).
                contentType("application/json").
        when().
                post(basePath + "rest/cars").
        then().
                statusCode(Status.CREATED.getStatusCode()).extract().asString();

        //new car should be there
        given().
        when().
                get(basePath + "rest/cars").
        then().
                statusCode(Status.OK.getStatusCode()).
                body("", hasSize(1)).
                body("model", hasItem("new car"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldFailToCreateCarWithoutName() {
        JsonObject carToCreate = new JsonObject();
        carToCreate.add("model", new JsonPrimitive("new car"));
        carToCreate.add("price", new JsonPrimitive(1000f));
        given().
                content(carToCreate.toString()).
                contentType("application/json").
        when().
                post(basePath + "rest/cars").
        then().
                statusCode(Status.BAD_REQUEST.getStatusCode()).
                body("message", equalTo("Car name cannot be empty"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldFailToCreateCarWithNonUniqueName() {
        JsonObject carToCreate = new JsonObject();
        carToCreate.add("model", new JsonPrimitive("new car"));
        carToCreate.add("name", new JsonPrimitive("ferrari spider"));
        carToCreate.add("price", new JsonPrimitive(1000f));
        given().
                content(carToCreate.toString()).
                contentType("application/json").
                when().
                post(basePath + "rest/cars").
                then().
                statusCode(Status.BAD_REQUEST.getStatusCode()).
                body("message", equalTo("Car name must be unique"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldUpdateCar() {
        JsonObject carToUpdate = new JsonObject();
        carToUpdate.add("id", new JsonPrimitive(1));
        carToUpdate.add("version", new JsonPrimitive(0));
        carToUpdate.add("model", new JsonPrimitive("Ferrari updated"));
        carToUpdate.add("name", new JsonPrimitive("Ferrari spider updated"));
        carToUpdate.add("price", new JsonPrimitive(1000f));
        given().
                content(carToUpdate.toString()).
                contentType("application/json").
         when().
                put(basePath + "rest/cars/1").  //dataset has car with id =1
         then().
                statusCode(Status.NO_CONTENT.getStatusCode());


    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldFailToDeleteCarWithoutAuthentication() {
        given().
                contentType(ContentType.JSON).
                when().
                delete(basePath + "rest/cars/1").  //dataset has car with id =1
                then().
                statusCode(Status.FORBIDDEN.getStatusCode());
    }


    @Test
    @UsingDataSet("car.yml")
    public void shouldFailToDeleteCarWithoutAuthorization() {
        given().
                contentType(ContentType.JSON).
                header("user", "guest"). //only admin can delete
        when().
                delete(basePath + "rest/cars/1").  //dataset has car with id =1
        then().
                statusCode(Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldDeleteCar() {
        given().
                contentType(ContentType.JSON).
                header("user", "admin").
        when().
                delete(basePath + "rest/cars/1").  //dataset has car with id =1
        then().
                statusCode(Status.NO_CONTENT.getStatusCode());

        //ferrari should not be in db anymore
        given().
                when().
                get(basePath + "rest/cars").
                then().
                statusCode(Status.OK.getStatusCode()).
                body("", hasSize(3)).
                body("model", not(hasItem("Ferrari")));
    }
}
