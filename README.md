Simple Java EE CDI crud, real pagination and (micro ;) ) services example.

This project is also referenced in the following posts:[cdi-crud-multi-tenancy](http://rpestano.wordpress.com/2014/11/04/cdi-crud-multi-tenancy/), [arquillian, cucumber and dbunit](http://rpestano.wordpress.com/2014/11/08/arquillian-cucumber-dbunit/), [Some words on JavaEE Rest and Swagger](http://rpestano.wordpress.com/2014/12/21/some-words-on-javaee-rest-and-swagger/) and [Testing your services with Arquillian and Docker](http://rpestano.wordpress.com/2014/12/28/testing-services-arquillian-docker/).

Since version 2 of this project there are 3 apps. Car services(almost the same app from [version 1](https://github.com/rmpestano/cdi-crud/tree/1.0.0) ), person crud which is an app that crud persons and consumes cars exposes by car service and commons app which gather utility classes used by the two apps.

The two apps are available online at Openshift PaaS: [Car Service](http://cdicrud-rpestano.rhcloud.com/car-service) and [Person Crud](http://person-rmpestano.rhcloud.com/person).

Note that [person project](https://github.com/rmpestano/cdi-crud/tree/master/person) only supports JavaEE 7(because it uses JAX-RS 2.0 client api) although [car service](https://github.com/rmpestano/cdi-crud/tree/master/car-service) still compatible with JavaEE 6 servers.

### How run it:

1. chose the maven profile: wildfly{default}, glassfish(4) or jboss(only car service)
2. OR run on your favorite IDE

### Running tests:

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
* JBossAS 7.1.1.Final(with weld 1.1.8 or above)

### Forge plugin
if you use forge(1.x) you may have a [look at this plugin](https://github.com/rmpestano/crud-plugin) which generates Crud in the format you see at this project. 

### Commons helper
If you want to use utility classes from [commons project](https://github.com/rmpestano/cdi-crud/tree/master/commons), you can use maven dependencies below:

```xml
<dependencies>
	<dependency>
		<groupId>com.cdi.crud</groupId>
		<artifactId>commons</artifactId>
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
### Continuous integration
[Travis-CI](https://travis-ci.org/rmpestano/cdi-crud) build:
[![Build Status](https://travis-ci.org/rmpestano/cdi-crud.png)](https://travis-ci.org/rmpestano/cdi-crud)

[Coveralls](https://coveralls.io/repos/rmpestano/cdi-crud/) coverage:
[![Coverage Status](https://coveralls.io/repos/rmpestano/cdi-crud/badge.png)](https://coveralls.io/r/rmpestano/cdi-crud)
