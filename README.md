## CDI Crud project Integration tests

this branch doesnt contain JSF frontend, only the backend (with *cars* rest endpoint) and integration tests

### How run it:

1. choose the maven profile(wildfly{default}, glassfish(4) or jboss)
2. run on your favourite IDE

### Runnig tests:

* via maven: mvn clean test -Ptests -Pwildfly-managed 
* or via IDE: activate container profile in your IDE and Run CrudIt.java|CrudBdd.java|CrudRest|CrudAt as Junit test


### Technologies:

* CDI
* Hibernate
* Deltaspike
* Arquillian
* Cucumber
* DBUnit
* JaxRS
* Swagger


### Application servers
Tested under:
* Wildfly 8.x
* Glassfish 4.0
* JBossAS 7.2.0.Final

