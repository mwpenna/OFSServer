package com.ofs.server.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class RepositoryInitialization implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    ConnectionManager connectionManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("Starting Initialization of buckets");
        Map<String, Object> repositories =  applicationReadyEvent.getApplicationContext().getBeansWithAnnotation(Repository.class);

        repositories.forEach((key, object) -> {
            String bucketName = repositories.get("advancedPersonController").getClass().getAnnotation(Repository.class).value();
            log.info("Opening connection to bucket: {}", bucketName);
            connectionManager.getBucket(bucketName);
        });
        log.info("Completed Initialization of buckets");
    }
}
