FROM airhacks/wildfly
COPY ./target/cdi-crud.war ${DEPLOYMENT_DIR}