#!/bin/sh
mvn clean package && docker build -t rmpestano/cdi-crud . && docker run -it --rm --name cdi-crud -p 8080:8080 -p 9990:9990 rmpestano/cdi-crud
