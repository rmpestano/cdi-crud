package com.cdi.crud.test;

import java.io.File;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.persistence.dbunit.dataset.Row;
import org.jboss.arquillian.persistence.dbunit.dataset.Table;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSet;
import org.jboss.arquillian.persistence.dbunit.dataset.yaml.YamlDataSetProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import com.cdi.crud.Crud;
import com.cdi.crud.bean.CrudBean;
import com.cdi.crud.test.dbunit.DBUnitRest;
import com.cdi.crud.test.dbunit.DBUnitUtils;

/**
 * @author rafael-pestano 20/07/2013 16:49:34
 *         <p/>
 *         Arquillian WebArchive factory
 */
@ArquillianSuiteDeployment
public class Deployments {


    protected static final String WEBAPP_SRC = "src/main/webapp";
    protected static final String WEB_INF= "src/main/webapp/WEB-INF";

    /**
     * @return base WebArchive for all arquillian tests
     */
    @Deployment(name="it",order=1)
    @UsingDataSet("car.yml")
    public static WebArchive getBaseDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class);
        war.addPackages(true, "com.cdi.crud.model");
        war.addPackages(true, "com.cdi.crud.service");
        war.addPackages(true, "com.cdi.crud.exception");
        war.addPackages(true, "com.cdi.crud.persistence");
        war.addPackages(true, "com.cdi.crud.qualifier");
        war.addPackages(true, "com.cdi.crud.util").
        addClass(Crud.class);
        //war.addClass(Datasource.class).
        war.addClass(CrudIt.class).//only needed by SuiteExtension
        addClass(MultiTenantIt.class).
        addClass(Deployments.class);
        //LIBS
        MavenResolverSystem resolver = Maven.resolver();
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.primefaces:primefaces:5.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.primefaces.themes:all-themes:1.0.10").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-jsf-module-api:1.1.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-jsf-module-impl:1.1.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.core:deltaspike-core-impl:1.1.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.core:deltaspike-core-api:1.1.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-security-module-api:1.1.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.assertj:assertj-core:1.7.0").withoutTransitivity().asSingleFile());
       // war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.h2database:h2:1.3.169").withoutTransitivity().asSingleFile());
  
        

        //WEB-INF

        war.addAsWebInfResource(new File(WEB_INF,"beans.xml"), "beans.xml");
        war.addAsWebInfResource(new File(WEB_INF,"web.xml"), "web.xml");
        war.addAsWebInfResource(new File(WEB_INF,"jbossas-ds.xml"), "jbossas-ds.xml");
        war.addAsWebInfResource(new File(WEB_INF,"faces-config.xml"), "faces-config.xml");

        //resources
        war.addAsResource("persistence.xml", "META-INF/persistence.xml");

        return war;
    }
    
    
    @Deployment(name = "bdd",order=2)
    public static Archive<?> createBddDeployment() {
        WebArchive war = Deployments.getBaseDeployment();
        war.delete("/WEB-INF/jbossas-ds.xml");
        war.addAsResource("datasets/car.yml", "car.yml").//needed by DBUnitUtils
                addClass(DBUnitUtils.class);
        System.out.println(war.toString(true));
        return war;
    }
    
    
    @Deployment(name = "at", testable=false,order=3)
    public static Archive<?> createAtDeployment() {
      WebArchive war = Deployments.getBaseDeployment();
          war.addAsResource("datasets/car.yml","car.yml").//needed by DBUnitUtils
                  addPackage(DBUnitUtils.class.getPackage()).addClass(CrudBean.class).addClass(YamlDataSet.class).
                  addClass(YamlDataSetProducer.class).
                  addClass(Row.class).addClass(Table.class).addClass(DBUnitRest.class);
          war.delete("/WEB-INF/jbossas-ds.xml");
          war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class).importDirectory("src/main/webapp").as(GenericArchive.class), "/", Filters.include(".*\\.(xhtml|html|css|js|png|gif)$"));
          MavenResolverSystem resolver = Maven.resolver();
          war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.dbunit:dbunit:2.5.0").withoutTransitivity().asSingleFile());
          war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.yaml:snakeyaml:1.10").withoutTransitivity().asSingleFile());
      System.out.println(war.toString(true));
      return war;
    }
    
    /**
     * @return base WebArchive for all arquillian tests
     */
    @Deployment(name="tenant",order=4)//tenant deployment doesnt use dataset
    //@UsingDataSet("car.yml")
    public static WebArchive getTenantDeployment() {
      WebArchive war = Deployments.getBaseDeployment();
      war.delete("/WEB-INF/jbossas-ds.xml");
        return war;
    }
    

}
