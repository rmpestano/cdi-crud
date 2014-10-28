Simple CDI crud and real pagination using generic dao and (micro ;))Service pattern.

A refactored version of https://github.com/rmpestano/cdi-generic-dao, created this new repo cause that one is referenced in the post: http://rpestano.wordpress.com/2013/07/15/cdi-generic-dao/


How run it:

1. choose the maven profile(wildfly{default}, glassfish(4) or jboss)
2. run on your favourite IDE
3. running tests use maven command: mvn clean test -Ptests -Pwildfly-managed or activate contAiner profile in your IDE and Run CrudIt.java or CrudBdd.java as Junit test



Technologies:

* CDI
* JSF
* Hibernate
* Primefaces
* Deltaspike
* Arquillian



Tested under Wildfly 8.0, Glassfish 4.0 and JBossAS 7.2.0.Final

[Travis-CI](https://travis-ci.org/rmpestano/cdi-crud) build:
[![Build Status](https://travis-ci.org/rmpestano/cdi-crud.png)](https://travis-ci.org/rmpestano/cdi-crud)

[Coveralls](https://coveralls.io/repos/rmpestano/cdi-crud/) coverage:
[![Coverage Status](https://coveralls.io/repos/rmpestano/cdi-crud/badge.png)](https://coveralls.io/r/rmpestano/cdi-crud)
