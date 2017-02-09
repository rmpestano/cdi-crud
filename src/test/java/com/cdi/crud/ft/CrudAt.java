package com.cdi.crud.ft;

import com.cdi.crud.bean.CarBean;
import com.cdi.crud.ft.pages.LogonDialog;
import com.cdi.crud.util.DBUnitUtils;
import com.cdi.crud.ft.pages.IndexPage;
import com.cdi.crud.util.Deployments;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.arquillian.ArquillianCucumber;
import cucumber.runtime.arquillian.api.Features;
import cucumber.runtime.arquillian.api.Tags;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.GrapheneElement;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
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

import java.io.File;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Car acceptance tests
 */

@RunWith(ArquillianCucumber.class)
@Features({"features/search-cars.feature", "features/remove-cars.feature"})
@Tags("@blackbox")
public class CrudAt {

    @Deployment(name = "cdi-crud.war", testable = false)
    public static Archive<?> createDeployment() {
        WebArchive war = Deployments.getBaseDeployment();
        war.addAsResource("datasets/car.yml", "car.yml").//needed by DBUnitUtils
                addClass(CarBean.class).addClass(YamlDataSet.class).
                addClass(YamlDataSetProducer.class).
                addClass(Row.class).addClass(Table.class);

        war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class).importDirectory("src/main/webapp").as(GenericArchive.class), "/", Filters.include(".*\\.(xhtml|html|css|js|png|gif)$"));
        MavenResolverSystem resolver = Maven.resolver();
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.github.adminfaces:admin-theme").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.github.adminfaces:admin-template").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.dbunit:dbunit:2.5.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.yaml:snakeyaml:1.10").withoutTransitivity().asSingleFile());
        war.addAsResource(new File("src/main/resources/admin-config.properties"), "admin-config.properties");
        war.addAsResource(new File("src/main/resources/crud.properties"), "crud.properties");
        System.out.println(war.toString(true));
        return war;
    }

    @ArquillianResource
    URL url;

    @Drone
    WebDriver webDriver;

    @FindByJQuery("div.ui-growl-message")
    private GrapheneElement growl;

    @FindByJQuery("a[id$=openLogin]")
    private GrapheneElement anchorLogin;

    @FindByJQuery("div[id$=userPanel]")
    private GrapheneElement divLogin;

    @Page
    IndexPage index;

    @FindByJQuery("div[id$=logonPanel]")
    LogonDialog logonPanel;


    @Before
    public void initDataset() {
        DBUnitUtils.createRemoteDataset(url, "car.yml");
    }

    @After
    public void clear() {
        DBUnitUtils.deleteRemoteDataset(url, "car.yml");
    }

    @When("^search car by id (\\d+)$")
    public void searchCarById(int id) {
        Graphene.goTo(IndexPage.class);
        index.findById("" + id);
    }

    @Then("^must find car with model \"([^\"]*)\" and price (.+)$")
    public void returnCarsWithModel(String model, final double price) {
        assertEquals(model, index.getInputModel().getAttribute("value"));
        assertEquals(price, Double.parseDouble(index.getInputPrice().getAttribute("value")), 0);
    }

    @Given("^user is logged in as \"([^\"]*)\"$")
    public void user_is_logged_in_as(String user) throws Throwable {
        Graphene.goTo(IndexPage.class);
        anchorLogin.click();
        waitModel().until().element(logonPanel.getUser()).is().present();
        logonPanel.doLogon(user);
        assertThat(divLogin.getText()).isEqualTo(user);
    }

    @And("^click on remove button$")
    public void click_on_remove_button() throws Throwable {
        index.remove();
    }

    @Then("^message \"([^\"]*)\" should be displayed$")
    public void message_should_be_displayed(String msg) throws Throwable {
        assertThat(growl.getText()).isEqualTo(msg);
    }

}
