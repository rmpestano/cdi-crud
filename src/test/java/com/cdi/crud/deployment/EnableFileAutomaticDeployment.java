package com.cdi.crud.deployment;

import java.io.File;
import org.jboss.arquillian.container.test.spi.client.deployment.AutomaticDeployment;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class EnableFileAutomaticDeployment implements AutomaticDeployment {

    public DeploymentContent generateDeploymentScenario(TestClass testClass) {

        if (testClass.isAnnotationPresent(EnableFileDeployment.class)) {

            final EnableFileDeployment enableFileDeployment = testClass.getAnnotation(EnableFileDeployment.class);
            final String deploymentFileLocation = enableFileDeployment.value();

            Archive deploymentFile = ShrinkWrap
                .create(ZipImporter.class, getName(deploymentFileLocation))
                .importFrom(new File(".", deploymentFileLocation))
                .as(WebArchive.class);

            DeploymentContentBuilder deploymentContentBuilder = new DeploymentContentBuilder(deploymentFile);
            return deploymentContentBuilder.withDeployment()
                .withTestable(enableFileDeployment.testable())
                .build()
                .get();

        }

        return null;
    }

    private String getName(String deploymentFileLocation) {
        return deploymentFileLocation.substring(deploymentFileLocation.lastIndexOf('/') + 1);
    }

}
