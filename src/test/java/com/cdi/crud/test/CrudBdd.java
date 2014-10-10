package com.cdi.crud.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import com.cdi.crud.model.Car;
import com.cdi.crud.service.CarService;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;

@RunWith(ArquillianCucumber.class)
//@RunWith(CukeSpace.class)
@Features("src/test/java/com/cdi/crud/test/CrudBdd.feature")
//@CucumberOptions(strict = true)
public class CrudBdd{
  
  @Inject
  CarService carService;
  
  Car carFound;
  
  
  @Deployment(name = "cdi-crud.war")
  public static Archive<?> createDeployment() {
      WebArchive war = Deployments.getBaseDeployment();
      System.out.println(war.toString(true));
      return war;
  }

  
  @Given("^search car with model \"([^\"]*)\"$")
  @UsingDataSet("car.yml")//dataset has car with model = "Ferrari"
  public void given(String model){
    Car carExample = new Car();
    carExample.setModel(model);
    carFound = carService.findByExample(carExample);
    assertNotNull(carFound);//is returning null cause dataset is being ignored
  }
  
  @When("^update model to \"([^\"]*)\"$")
  public void when(String model) {
    carFound.setModel(model);
    carService.update(carFound);
  }
  
  @Then("^searching car by model \"([^\"]*)\" must return (\\d+) of records$")
  public void then(final String model, final int result){
    Car carExample = new Car();
    carExample.setModel(model);
    assertEquals(result,carService.crud().example(carExample).count());
  }
  
  
}
