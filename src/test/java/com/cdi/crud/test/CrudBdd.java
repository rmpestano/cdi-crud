package com.cdi.crud.test;

import com.cdi.crud.model.Car;
import com.cdi.crud.service.CarService;
import com.cdi.crud.test.dbunit.DBUnitUtils;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;
import cucumber.runtime.arquillian.api.Tags;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ArquillianCucumber.class)
@Features("features/search-cars.feature")
@Tags("@whitebox")
public class CrudBdd {

    @Inject
    CarService carService;

    Car carFound;

    int numCarsFound;

    @Deployment(name = "cdi-crud.war")
    public static Archive<?> createDeployment() {
        WebArchive war = Deployments.getBaseDeployment();
        war.addAsResource("datasets/car.yml", "car.yml").//needed by DBUnitUtils
                addClass(DBUnitUtils.class);
        System.out.println(war.toString(true));
        return war;
    }


    @Before
    public void initDataset() {
        DBUnitUtils.createDataset("car.yml");
    }

    @After
    public void clear() {
        DBUnitUtils.deleteDataset("car.yml");
    }

    @Given("^search car with model \"([^\"]*)\"$")
    // @UsingDataSet("car.yml")//dataset has car with model = "Ferrari",
    // usingDataset commented because of issue:
    // https://github.com/cukespace/cukespace/issues/37
    public void searchCarWithModel(String model) {
        Car carExample = new Car();
        carExample.setModel(model);
        carFound = carService.findByExample(carExample);
        assertNotNull(carFound);
    }

    @When("^update model to \"([^\"]*)\"$")
    public void updateModel(String model) {
        carFound.setModel(model);
        carService.update(carFound);
    }

    @Then("^searching car by model \"([^\"]*)\" must return (\\d+) of records$")
    public void searchingCarByModel(final String model, final int result) {
        Car carExample = new Car();
        carExample.setModel(model);
        assertEquals(result, carService.crud().example(carExample).count());
    }

    @When("^search car with price less than (.+)$")
    public void searchCarWithPrice(final double price) {
        numCarsFound = carService.crud().initCriteria().le("price", price).count();
    }

    @Then("^must return (\\d+) cars")
    public void mustReturnCars(final int result) {
        assertEquals(result, numCarsFound);
    }

}
