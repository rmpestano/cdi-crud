package com.cdi.crud.it;

import com.cdi.crud.infra.exception.CustomException;
import com.cdi.crud.infra.security.CustomAuthorizer;
import com.cdi.crud.model.Car;
import com.cdi.crud.service.CarService;
import com.cdi.crud.util.DBUnitUtils;
import com.cdi.crud.util.Deployments;
import cucumber.api.PendingException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ArquillianCucumber.class)
@Features({"features/search-cars.feature", "features/remove-cars.feature"})
@Tags("@whitebox")
public class CrudBdd {

    @Inject
    CarService carService;

    @Inject
    CustomAuthorizer authorizer;

    Car carFound;

    int numCarsFound;

    String message;

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
    // https://github.com/cukespace/cukespace/issues/37 and https://issues.jboss.org/browse/ARQ-1883
    public void searchCarWithModel(String model) {
        Car carExample = new Car().model(model);
        carFound = carService.findByExample(carExample);
        assertNotNull(carFound);
    }

    @When("^update model to \"([^\"]*)\"$")
    public void updateModel(String model) {
        carFound.model(model);
        carService.update(carFound);
    }

    @Then("^searching car by model \"([^\"]*)\" must return (\\d+) of records$")
    public void searchingCarByModel(final String model, final int result) {
        Car carExample = new Car().model(model);
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


    @When("^\"([^\"]*)\" is removed$")
    public void ferrari_is_removed(String model) throws Throwable {
        assertThat(carFound.getModel()).isEqualTo(model);
        try {
            carService.remove(carFound);
        } catch (CustomException ex) {
            message = ex.getMessage();
        }
    }

    @Then("^there is no more cars with model \"([^\"]*)\"$")
    public void there_is_no_more_cars_with_model(String model) throws Throwable {

        assertThat(carService.crud().eq("model", model).count()).isEqualTo(0);
    }

    @Given("^user is logged in as \"([^\"]*)\"$")
    public void user_is_logged_in_as(String user) throws Throwable {
        authorizer.login(user);
        assertThat(authorizer.getCurrentUser().get("user")).isEqualTo(user);
    }

    @Then("^error message must be \"([^\"]*)\"$")
    public void error_message_must_be(String msg) throws Throwable {
        // Express the Regexp above with the code you wish you had
        assertThat(msg).isEqualTo(message);
    }
}
