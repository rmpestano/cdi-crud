package com.cdi.crud.test;

import com.cdi.crud.bean.CrudBean;
import com.cdi.crud.test.dbunit.DBUnitRest;
import com.cdi.crud.test.dbunit.DBUnitUtils;
import com.cdi.crud.test.pages.IndexPage;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;
import cucumber.runtime.arquillian.api.Tags;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.persistence.dbunit.dataset.Row;
import org.jboss.arquillian.persistence.dbunit.dataset.Table;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSet;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSetProducer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(ArquillianCucumber.class)
@Features("features/search-cars.feature")
@Tags("@blackbox")
public class CrudAt {
  
  
  
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
