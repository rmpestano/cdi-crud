/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdi.crud.person.service;

import com.cdi.crud.commons.CrudService;
import com.cdi.crud.commons.exception.CustomException;
import com.cdi.crud.commons.model.Filter;
import com.cdi.crud.commons.security.Admin;
import com.cdi.crud.person.model.CarDto;
import com.cdi.crud.person.model.Person;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * @author rmpestano
 *         <p/>
 *         Car Business logic
 */
@Stateless
public class PersonService extends CrudService<Person> {

    @Inject
    @ConfigProperty(name = "carservice.endpoint.url")//system property
    private String carServiceUrl;

    @PostConstruct
    public void init(){
        if (carServiceUrl == null || "".equals(carServiceUrl)) {
            carServiceUrl = "http://localhost:8080/car-service/rest/";
        }
        if (!carServiceUrl.endsWith("/")) {
            carServiceUrl = carServiceUrl + "/";
        }
    }

    WebTarget getTarget(String resource){
        Client client = ClientBuilder.newClient();
        try {
            return client.target(URI.create(new URL(carServiceUrl + resource).toExternalForm()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {

        }
        return null;
    }


    @Override
    public Criteria configPagination(Filter<Person> filter) {
        if (filter.hasParam("id")) {
            crud().eq("id",
                    Long.parseLong(filter.getParam("id").toString()));
        }

        // see index.xhtml 'model' column facet name filter
        if (filter.getEntity() != null) {
            crud().ilike("car", filter.getEntity().getCar(), MatchMode.ANYWHERE);
            crud().ilike("name", filter.getEntity().getName(), MatchMode.ANYWHERE);
        }
        return crud().getCriteria();
    }

    @Override
    public void beforeInsert(Person person) {
        if (!person.hasName()) {
            throw new CustomException("Person name cannot be empty");
        }
        if (!person.hasCar()) {
            throw new CustomException("Person car cannot be empty");
        }

        if (crud().eq("name", person.getName()).ne("id", person.getId()).count() > 0) {
            throw new CustomException("Person name must be unique");
        }
    }

    @Override
    public void beforeUpdate(Person entity) {
        this.beforeInsert(entity);
    }


    @Override
    @Admin
    public void remove(Person person) {
        super.remove(person);
    }

    public int countCars(Filter<CarDto> carFilter) {
        Response response = getTarget("cars/count").queryParam("model", carFilter.getEntity() != null ? carFilter.getEntity().getModel():null).
                queryParam("name", carFilter.getEntity() != null ? carFilter.getEntity().getName():null).request(MediaType.APPLICATION_JSON).get();
        if(response.getStatus() == 200){
            return Integer.parseInt(response.readEntity(String.class));
        }
        if(response.getStatus() == 404){
            throw new CustomException("Car service is unavailable, try again later.");
        }
        else{
            throw new CustomException("Problems trying to fetch cars from service.\n error:"+response.getEntity());
        }

    }

    public int getCarServiceStatus() {
        return getTarget("cars/count").request().get().getStatus();
    }

    public List<CarDto> listCars(Filter<CarDto> carFilter) {
        Response response = getTarget("cars/list").queryParam("start", carFilter.getFirst()).queryParam("max",carFilter.getPageSize()).queryParam("model",carFilter.getEntity() != null ? carFilter.getEntity().getModel():null).
                queryParam("name",carFilter.getEntity().getName() != null ? carFilter.getEntity().getName():null).request(MediaType.APPLICATION_JSON).get();
        if(response.getStatus() == 200){
            List<CarDto> cars = response.readEntity(new GenericType<List<CarDto>>() {
            });

            response.close();
            return cars;
        } else{
            if(response.getStatus() == 404){
                throw new CustomException("Car service unavailable, try again later.");
            }
            else{
                throw new CustomException("Problems trying to fetch cars from service.\n error:"+response.getEntity());
            }
        }
    }
}
