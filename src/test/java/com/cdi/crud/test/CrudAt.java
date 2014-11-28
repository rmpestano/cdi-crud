package com.cdi.crud.test;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import com.cdi.crud.test.dbunit.DBUnitUtils;
import com.cdi.crud.test.pages.IndexPage;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;
import cucumber.runtime.arquillian.api.Tags;

@RunWith(ArquillianCucumber.class)
@Features("features/search-cars.feature")
@Tags("@blackbox")
public class CrudAt extends Deployments{
  
  
  
  @ArquillianResource
  URL url;
  
  @Drone
  WebDriver webDriver;

  @Page
  IndexPage index;


  @Before
  public void initDataset() {
      DBUnitUtils.createRemoteDataset(url,"car.yml");
  }

  @After
  public void clear(){
      DBUnitUtils.deleteRemoteDataset(url,"car.yml");
   }
  
  @When("^search car by id (\\d+)$")
  @OperateOnDeployment("at")
  public void searchCarById(int id){
      Graphene.goTo(IndexPage.class);
      index.findById(""+id);
  }
  
  @Then("^must find car with model \"([^\"]*)\" and price (.+)$")
  @OperateOnDeployment("at")
  public void returnCarsWithModel(String model, final double price){
    assertEquals(model,index.getInputModel().getAttribute("value"));
    assertEquals(price,Double.parseDouble(index.getInputPrice().getAttribute("value")),0);
  }

}
