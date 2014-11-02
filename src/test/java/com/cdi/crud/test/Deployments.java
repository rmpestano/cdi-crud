package com.cdi.crud.test;

import com.cdi.crud.Crud;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import java.io.File;

/**
 * @author rafael-pestano 20/07/2013 16:49:34
 *         <p/>
 *         Arquillian WebArchive factory
 */
public class Deployments {


    protected static final String WEBAPP_SRC = "src/main/webapp";
    protected static final String WEB_INF= "src/main/webapp/WEB-INF";

    /**
     * @return base WebArchive for all arquillian tests
     */
    public static WebArchive getBaseDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class);
        war.addPackages(true, "com.cdi.crud.model");
        war.addPackages(true, "com.cdi.crud.service");
        war.addPackages(true, "com.cdi.crud.exception");
        war.addPackages(true, "com.cdi.crud.util").
        addClass(Crud.class);
        //LIBS
        MavenResolverSystem resolver = Maven.resolver();
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.primefaces:primefaces:5.0").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.primefaces.themes:all-themes:1.0.10").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-jsf-module-api:1.0.3").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-jsf-module-impl:1.0.3").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.core:deltaspike-core-impl:1.0.3").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.core:deltaspike-core-api:1.0.3").withoutTransitivity().asSingleFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-security-module-api:1.0.3").withoutTransitivity().asSingleFile());
        //WEB-INF

        war.addAsWebInfResource(new File(WEB_INF,"beans.xml"), "beans.xml");
        war.addAsWebInfResource(new File(WEB_INF,"web.xml"), "web.xml");
        war.addAsWebInfResource(new File(WEB_INF,"faces-config.xml"), "faces-config.xml");

        //resources
        war.addAsResource("persistence.xml", "META-INF/persistence.xml");

        return war;
    }

}
