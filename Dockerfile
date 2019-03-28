FROM rmpestano/wildfly:16.0.0
COPY ./target/cdi-crud.war ${DEPLOYMENT_DIR}