package com.dickson.libraryapp;

import com.dickson.libraryapp.config.AsyncSyncConfiguration;
import com.dickson.libraryapp.config.DatabaseTestcontainer;
import com.dickson.libraryapp.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        LibraryManagementApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.dickson.libraryapp.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
