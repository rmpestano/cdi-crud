FROM rmpestano/wildfly:16.0.1
COPY ./target/cdi-crud.war ${DEPLOYMENT_DIR}