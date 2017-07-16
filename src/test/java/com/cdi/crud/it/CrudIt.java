package com.cdi.crud.it;

import com.cdi.crud.model.Car;
import com.cdi.crud.service.CarService;
import com.cdi.crud.infra.exception.CustomException;
import com.cdi.crud.infra.model.Filter;
import com.cdi.crud.infra.model.SortOrder;
import com.cdi.crud.infra.security.CustomAuthorizer;
import com.cdi.crud.util.Deployments;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Created by rmpestano on 9/7/14.
 */
@RunWith(Arquillian.class)
public class CrudIt {

    @Inject
    CarService carService;

    @Inject
    CustomAuthorizer authorizer;

    @Deployment(name = "cdi-crud.war")
    public static Archive<?> createDeployment() {
        WebArchive war = Deployments.getBaseDeployment();
        System.out.println(war.toString(true));
        return war;
    }

    @Test
    public void shouldBeInitialized() {
        assertNotNull(carService);
        assertEquals(carService.crud().countAll(), 0);
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldCountCars() {
        assertEquals(carService.crud().countAll(), 4);
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldFindCarById() {
        Car car = carService.findById(1);
        assertNotNull(car);
        assertEquals(car.getId(),new Integer(1));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldFindCarByExample() {
        Car carExample = new Car().model("Ferrari");
        Car car = carService.findByExample(carExample);
        assertNotNull(car);
        assertEquals(car.getId(),new Integer(1));
    }

    @Test
    public void shouldNotInsertCarWithoutName(){
        int countBefore = carService.count(new Filter<Car>());
        assertEquals(countBefore,0);
        Car newCar = new Car().model("My Car").price(1d);
        try {
            carService.insert(newCar);
        }catch (CustomException e){
            assertEquals("Car name cannot be empty",e.getMessage());
        }
    }

    @Test
    public void shouldNotInsertCarWithoutModel(){
        Car newCar = new Car().name("My Car").price(1d);
        try {
            carService.insert(newCar);
        }catch (CustomException e){
            assertEquals("Car model cannot be empty",e.getMessage());
        }
    }

    @Test
    @UsingDataSet("car.yml")
    @Cleanup(phase = TestExecutionPhase.BEFORE)
    public void shouldNotInsertCarWithDuplicateName(){
        Car newCar = new Car().model("My Car").name("ferrari spider").price(1d);
        try {
            carService.insert(newCar);
        }catch (CustomException e){
            assertEquals("Car name must be unique",e.getMessage());
        }
    }

    @Test
    public void shouldInsertCar(){
        int countBefore = carService.count(new Filter<Car>());
        assertEquals(countBefore,0);
        Car newCar = new Car().model("My Car").name("car name").price(1d);
        carService.insert(newCar);
        assertEquals(countBefore + 1, carService.count(new Filter<Car>()));
    }

    @Test
    public void shouldNotRemoveCarWithUnauthorizedUser(){
        authorizer.login("guest");
        try {
            carService.remove(new Car(1));
        }catch (CustomException e){
           assertEquals("Access denied",e.getMessage());
        }
    }

    @Test
    @UsingDataSet("car.yml")
    @Transactional(TransactionMode.DISABLED)
    public void shouldRemoveCar(){
        authorizer.login("admin");
        Car car = carService.findById(1);
        assertNotNull(car);
        carService.remove(car);
        assertNull(carService.findById(1));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldListCarsModel(){
        List<Car> cars = carService.listByModel("porche");
        assertNotNull(cars);
        assertEquals(cars.size(),2);
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldPaginateCars(){
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(1);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertEquals(cars.get(0).getId(),new Integer(1));
        carFilter.setFirst(1);//get second database page
        cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertEquals(cars.get(0).getId(),new Integer(2));
        carFilter.setFirst(0);
        carFilter.setPageSize(4);
        cars = carService.paginate(carFilter);
        assertEquals(cars.size(),4);
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldPaginateAndSortCars(){
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(4).setSortField("model").setSortOrder(SortOrder.DESCENDING);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(),4);
        assertTrue(cars.get(0).getModel().equals("Porche274"));
        assertTrue(cars.get(3).getModel().equals("Ferrari"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldPaginateCarsByModel(){
        Car carExample = new Car().model("Ferrari");
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(4).setEntity(carExample);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertTrue(cars.get(0).getModel().equals("Ferrari"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldPaginateCarsByPrice(){
        Car carExample = new Car().price(12999.0);
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(2).setEntity(carExample);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertTrue(cars.get(0).getModel().equals("Mustang"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldPaginateCarsByIdInParam(){
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(2).addParam("id",1);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertTrue(cars.get(0).getId().equals(new Integer(1)));
    }

    @Test
    @UsingDataSet("car.yml")
    @Transactional(value=TransactionMode.DISABLED)
    public void shouldListCarsByPrice(){
        List<Car> cars = carService.crud().between("price", (double) 1000, (double) 2450.9).addOrderAsc("price").list();
        //ferrari and porche
        assertNotNull(cars);
        assertEquals(cars.size(),2);
        assertEquals(cars.get(0).getModel(), "Porche");
        assertEquals(cars.get(1).getModel(), "Ferrari");
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldGetCarModels(){
        List<String> models = carService.getModels("po");
        //porche and Porche274
        assertNotNull(models);
        assertEquals(models.size(),2);
        assertTrue(models.contains("Porche"));
        assertTrue(models.contains("Porche274"));
    }
}
