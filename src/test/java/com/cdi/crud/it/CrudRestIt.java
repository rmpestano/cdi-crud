package com.cdi.crud.it;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response.Status;

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

import com.cdi.crud.util.Deployments;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

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

        JsonObject jsonObject = Json.createReader(new StringReader(json)).readObject();
        assertEquals("Ferrari", jsonObject.getString("model"));

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
    @UsingDataSet("empty.yml")
    public void shouldCreateCar() {
        JsonObject carToCreate = Json.createObjectBuilder()
        .add("model", "new car")
        .add("name", "new car name")
        .add("price", 1000).build();
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
    	JsonObject carToCreate = Json.createObjectBuilder()
    		        .add("model", "new car")
    		        .add("price", 1000).build();
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
    	
		JsonObject carToCreate = Json.createObjectBuilder()
				.add("model", "new car").add("name", "ferrari spider")
				.add("price", 1000).build();
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
    	JsonObject carToUpdate = Json.createObjectBuilder()
    			.add("id", 1)
    			.add("version", 0)
				.add("model", "Ferrari updated")
				.add("name", "ferrari spider updated")
				.add("price", 1000).build();
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
