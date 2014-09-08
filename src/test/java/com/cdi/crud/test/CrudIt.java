package com.cdi.crud.test;

import com.cdi.crud.model.Car;
import com.cdi.crud.model.Filter;
import com.cdi.crud.service.CarService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.model.SortOrder;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by rmpestano on 9/7/14.
 */
@RunWith(Arquillian.class)
public class CrudIt {

    @Inject
    CarService carService;

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
        Car carExample = new Car();
        carExample.setModel("Ferrari");
        Car car = carService.findByExample(carExample);
        assertNotNull(car);
        assertEquals(car.getId(),new Integer(1));
    }

    @Test
    public void shouldInsertCar(){
        int countBefore = carService.count(new Filter<Car>());
        assertEquals(countBefore,0);
        Car newCar = new Car("My Car", 1);
        carService.insert(newCar);
        assertEquals(countBefore + 1, carService.count(new Filter<Car>()));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldRemoveCar(){
        int countBefore = carService.count(new Filter<Car>());
        assertEquals(countBefore,4);
        carService.remove(new Car(1));
        assertEquals(countBefore-1, carService.count(new Filter<Car>()));
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
        Car carExample = new Car();
        carExample.setModel("Ferrari");
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(4).setEntity(carExample);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertTrue(cars.get(0).getModel().equals("Ferrari"));
    }

    @Test
    @UsingDataSet("car.yml")
    public void shouldListCarsByPrice(){
        List<Car> cars = carService.crud().between("price", (double) 1000, (double) 2450.9).addOrderAsc("price").list();
        //ferrari and porche
        assertNotNull(cars);
        assertEquals(cars.size(),2);
        assertEquals(cars.get(0).getModel(), "Porche");
        assertEquals(cars.get(1).getModel(), "Ferrari");
    }
}
