package com.cdi.crud.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import com.cdi.crud.model.Car;
import com.cdi.crud.service.CarService;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;

@RunWith(ArquillianCucumber.class)
// @RunWith(CukeSpace.class)
@Features("src/test/java/com/cdi/crud/test/CrudBdd.feature")
// @CucumberOptions(strict = true)
public class CrudBdd {

	@Inject
	CarService carService;

	Car carFound;
	
	int numCarsFound;

	@Deployment(name = "cdi-crud.war")
	public static Archive<?> createDeployment() {
		WebArchive war = Deployments.getBaseDeployment();
		System.out.println(war.toString(true));
		return war;
	}


	@Before
	public void initDataset() {
		/** same as car.yml
		 */
		carService.remove(carService.listAll());
		
		Car ferrari = new Car("Ferrari",2450.8d);
		
		Car mustang = new Car("Mustang",12999.0d);
		
		Car porche = new Car("Porche",1390.3d);
		
		Car porche274 = new Car("Porche274",18990.23);
		
		carService.insert(ferrari);
		carService.insert(mustang);
		carService.insert(porche);
		carService.insert(porche274);

	}

	@Given("^search car with model \"([^\"]*)\"$")
	// @UsingDataSet("car.yml")//dataset has car with model = "Ferrari",
	// usingDataset commented because of issue:
	// https://github.com/cukespace/cukespace/issues/37
	public void searchCarWithModel(String model) {
		Car carExample = new Car();
		List<Car> cars = carService.listAll();
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
	public void searchCarWithPrice(final double price){
		numCarsFound = carService.crud().le("price", price).count();
	}
	
	@Then("^must return (\\d+) cars")
	public void mustReturnCars(final int result){
		assertEquals(result,numCarsFound);
	}
}
