package com.cdi.crud.test;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@DataSourceDefinition(name = "java:jboss/datasources/ExampleDS2",  url = "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1", className = "org.h2.Driver", user = "sa", password = "", databaseName="ExampleDS2")
@Singleton
@Startup
public class Datasource {

}
