Simple Java EE CDI crud, real pagination and (micro ;) ) services example.

This project is also referenced in the following posts:[cdi-crud-multi-tenancy](http://rpestano.wordpress.com/2014/11/04/cdi-crud-multi-tenancy/), [arquillian, cucumber and dbunit](http://rpestano.wordpress.com/2014/11/08/arquillian-cucumber-dbunit/), [Some words on JavaEE Rest and Swagger](http://rpestano.wordpress.com/2014/12/21/some-words-on-javaee-rest-and-swagger/) and [Testing your services with Arquillian and Docker](http://rpestano.wordpress.com/2014/12/28/testing-services-arquillian-docker/).

The two apps are available online at Openshift PaaS: [Car Service](http://cdicrud-rpestano.rhcloud.com/car-service) and [Person Crud](http://person-rmpestano.rhcloud.com/person).

### How run it:

1. choose the maven profile(wildfly{default}, glassfish(4) or jboss)
2. run on your favourite IDE

### Runnig tests:

* via maven: mvn clean test -Ptests -Pwildfly-managed 
* or via IDE: activate container profile in your IDE and Run CrudIt.java|CrudBdd.java|CrudRest|CrudAt as Junit test


### Technologies:

* CDI
* JSF
* Hibernate
* Primefaces
* Deltaspike
* Arquillian
* Cucumber
* DBUnit
* JaxRS
* Swagger
* Openshift


### Application servers
Tested under:
* Wildfly 8.x
* Glassfish 4.0
* JBossAS 7.2.0.Final

### Forge plugin
if you use forge(1.x) you may have a [look at this plugin](https://github.com/rmpestano/crud-plugin) which generates Crud in the format you see at this project. 

### Crud helper 
Also if you want to import Crud utility class directly into your project, you can use maven dependencies below:

```xml
<dependencies>
	<dependency>
		<groupId>com.cdi.crud</groupId>
		<artifactId>cdi-crud</artifactId>
		<version>1.0.0</version>
	</dependency>
	<dependency>
		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-core</artifactId>
		<version>4.3.5.Final</version>
		<scope>provided</scope>
	</dependency>
	<dependency>
		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-validator</artifactId>
		<version>5.1.1.Final</version>
		<scope>provided</scope>
	</dependency>
	<dependency>
		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-entitymanager</artifactId>
		<version>4.3.5.Final</version>
		<scope>provided</scope>
	</dependency>
</dependencies>

<repositories>
     <repository>
        <id>cdi-crud maven repo</id>
        <url>http://cdi-crud-repo.googlecode.com/git/</url>
       <layout>default</layout>
    </repository>
</repositories>
```
### Continous integration
[Travis-CI](https://travis-ci.org/rmpestano/cdi-crud) build:
[![Build Status](https://travis-ci.org/rmpestano/cdi-crud.png)](https://travis-ci.org/rmpestano/cdi-crud)

[Coveralls](https://coveralls.io/repos/rmpestano/cdi-crud/) coverage:
[![Coverage Status](https://coveralls.io/repos/rmpestano/cdi-crud/badge.png)](https://coveralls.io/r/rmpestano/cdi-crud)
