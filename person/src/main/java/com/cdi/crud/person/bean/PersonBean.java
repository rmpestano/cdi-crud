/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdi.crud.person.bean;

import com.cdi.crud.commons.exception.CustomException;
import com.cdi.crud.commons.model.Filter;
import com.cdi.crud.person.model.CarDto;
import com.cdi.crud.person.model.Person;
import com.cdi.crud.person.service.PersonService;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author rmpestano
 */
@Named
@ViewAccessScoped
public class PersonBean implements Serializable {

    private LazyDataModel<Person> personList;
    private LazyDataModel<CarDto> carList;//fetched via rest
    private List<Person> filteredValue;// datatable filteredValue attribute
    private Long id;
    private Person person;
    private Filter<Person> filter = new Filter<Person>(new Person());
    private CarDto carSelection;
    private CarDto carDto = new CarDto();
    private boolean serviceAvailable;
    private String serviceErrorMessage;

    @Inject
    @ConfigProperty(name = "carservice.endpoint.url")//system property
    private String carServiceUrl;

    @Inject
    PersonService personService;


    @PostConstruct
    public void init() {
        if (personService.crud().countAll() == 0) {
            for (int i = 1; i <= 10; i++) {
                Person p = new Person().name("name " + i).car("car " + i);
                personService.insert(p);
            }
        }
        if (carServiceUrl == null || "".equals(carServiceUrl)) {
            carServiceUrl = "http://localhost:8080/car-service/rest/";
        }
        if (!carServiceUrl.endsWith("/")) {
            carServiceUrl = carServiceUrl + "/";
        }
    }



    public LazyDataModel<Person> getPersonList() {
        if (personList == null) {
            // usually in an utility or super class cause this code is always
            // the same
            personList = new LazyDataModel<Person>() {
                @Override
                public List<Person> load(int first, int pageSize,
                                      String sortField, SortOrder sortOrder,
                                      Map<String, Object> filters) {
                    com.cdi.crud.commons.model.SortOrder order = null;
                    if (sortOrder != null) {
                        order = sortOrder.equals(SortOrder.ASCENDING) ? com.cdi.crud.commons.model.SortOrder.ASCENDING
                                : sortOrder.equals(SortOrder.DESCENDING) ? com.cdi.crud.commons.model.SortOrder.DESCENDING
                                : com.cdi.crud.commons.model.SortOrder.UNSORTED;
                    }
                    filter.setFirst(first).setPageSize(pageSize)
                            .setSortField(sortField).setSortOrder(order)
                            .setParams(filters);
                    List<Person> list = personService.paginate(filter);
                    setRowCount(personService.count(filter));
                    return list;
                }

                @Override
                public int getRowCount() {
                    return super.getRowCount();
                }
            };

        }
        return personList;
    }

    public LazyDataModel<CarDto> getCarList() {
        return carList;
    }

    public void searchCars(){
        verifyServiceAvailability();
        carDto = new CarDto();
        if(carList == null){
            initCarDatamodel();
        }
    }

    private void verifyServiceAvailability() {
        Response response = getTarget("cars/count").request().get();
        if(response.getStatus() == 200){
            serviceAvailable = true;
        }
        else if(response.getStatus() == 404){
            serviceAvailable = false;
            throw new CustomException("Car service is unavailable, try again later.");
        }
        else{
            serviceAvailable = false;
            throw new CustomException("Problems trying to fetch cars from service.\n error:"+response.getEntity());
        }
    }

    private void initCarDatamodel() {
        carList = new LazyDataModel<CarDto>() {
            @Override
            public List<CarDto> load(int first, int pageSize,
                                     String sortField, SortOrder sortOrder,
                                     Map<String, Object> filters) {

                Response response = getTarget("cars/list").queryParam("start", first).queryParam("max",pageSize).queryParam("model",carDto != null ? carDto.getModel():null).
                        queryParam("name",carDto != null ? carDto.getName():null).request(MediaType.APPLICATION_JSON).get();
                if(response.getStatus() == 200){
                    List<CarDto> cars = response.readEntity(new GenericType<List<CarDto>>() {
                    });
                    setRowCount(Integer.parseInt(getTarget("cars/count").queryParam("model", carDto != null ? carDto.getModel():null).
                            queryParam("name", carDto != null ? carDto.getName():null).request(MediaType.APPLICATION_JSON).get(String.class)));
                    return cars;
                } else{
                    serviceAvailable = false;
                    if(response.getStatus() == 404){
                        serviceErrorMessage = "Car service unavailable, try again later.";
                        return null;
                    }
                    else{
                        serviceErrorMessage = "Problems trying to fetch cars from service.\n error:"+response.getEntity();
                        return null;
                    }
                }

            }

        };
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        if (person == null) {
            person = new Person();
        }
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public CarDto getCarDto() {
        return carDto;
    }

    public void setCarDto(CarDto carDto) {
        this.carDto = carDto;
    }

    public CarDto getCarSelection() {
        return carSelection;
    }

    public void setCarSelection(CarDto carSelection) {
        this.carSelection = carSelection;
    }

    public void findPersonById() {
        if (id == null) {
            throw new CustomException("Provide Person ID to load");
        }
        person = personService.findById(id);
        if (person == null) {
            throw new CustomException("Person not found with id " + id);
        }
    }

    public List<Person> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Person> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public void remove() {
        if (person != null && person.getId() != null) {
            personService.remove(person);
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage("Person " + person.getName()
                            + " removed successfully"));
            clear();
        }
    }

    public void update() {
        String msg;
        if (person.getId() == null) {
            personService.insert(person);
            msg = "Person " + person.getName() + " created successfully";
        } else {
            personService.update(person);
            msg = "Person " + person.getName() + " updated successfully";
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(msg));
        clear();// reload person list
    }

    public void clear() {
        person = new Person();
        filter = new Filter<Person>(new Person());
        id = null;
    }

    public void onRowSelect(SelectEvent event) {
        setId((Long) ((Person) event.getObject()).getId());
        findPersonById();
    }

    public void onCarSelect(SelectEvent event) {
        person.setCar(((CarDto) event.getObject()).getName());
    }

    public void onRowUnselect(UnselectEvent event) {
        person = new Person();
    }

    public Filter<Person> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Person> filter) {
        this.filter = filter;
    }

    public boolean isServiceAvailable() {
        return serviceAvailable;
    }

    public String getServiceErrorMessage() {
        return serviceErrorMessage;
    }

    WebTarget getTarget(String resource){
        Client client = ClientBuilder.newClient();
        try {
            return client.target(URI.create(new URL(carServiceUrl + resource).toExternalForm()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
