package com.cdi.crud.deployment;

import java.io.File;
import org.jboss.arquillian.container.test.spi.client.deployment.AutomaticDeployment;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;

public class EnableMavenBuildDeploymentAutomaticDeployment implements AutomaticDeployment {

    public DeploymentContent generateDeploymentScenario(TestClass testClass) {
        if (testClass.isAnnotationPresent(EnableMavenBuildDeployment.class)) {

            final Archive archive = EmbeddedMaven.forProject(new File("pom.xml"))
                .useMaven3Version("3.5.0")
                .setGoals("package")
                .setQuiet()
                .skipTests(true)
                .ignoreFailure()
                .build().getDefaultBuiltArchive();

            final EnableMavenBuildDeployment enableMavenBuildDeployment = testClass.getAnnotation(EnableMavenBuildDeployment.class);

            DeploymentContentBuilder deploymentContentBuilder = new DeploymentContentBuilder(archive);
            final DeploymentContent deploymentContent = deploymentContentBuilder
                .withDeployment()
                .withTestable(enableMavenBuildDeployment.testable())
                .build()
                .get();

            return deploymentContent;
        }
        return null;
    }
}
