package com.devops.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main application class configured to produce a deployable *.war file
 * for Apache Tomcat while also supporting standalone execution.
 */
@SpringBootApplication
public class DevOpsFlowApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // Required for deployment in external Apache Tomcat servlet container
        return application.sources(DevOpsFlowApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DevOpsFlowApplication.class, args);
    }
}
