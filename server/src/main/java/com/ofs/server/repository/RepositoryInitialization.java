package com.ofs.server.repository;


import com.couchbase.client.java.Bucket;
import com.ofs.server.errors.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

@Component
@Slf4j
public class RepositoryInitialization implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    ConnectionManager connectionManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("Starting Initialization and injection of buckets");
        Map<String, Object> repositories =  applicationReadyEvent.getApplicationContext().getBeansWithAnnotation(OFSRepository.class);

        repositories.forEach((key, object) -> {
            Class clazz = repositories.get(key).getClass();
            String bucketName = clazz.getClass().getAnnotation(OFSRepository.class).value();
            log.info("Opening connection to bucket: {}", bucketName);
            Bucket bucket = connectionManager.getBucket(bucketName);
            log.info("Completed opening connection to bucket: {}", bucketName);
            log.info("Starting to inject bucket into repository class: {}" + clazz.getSimpleName());
            injectBucketIntoRepositoryClass(clazz, bucket);
            log.info("Finsihed to inject bucket into repository class: {}" + clazz.getSimpleName());
        });
        log.info("Completed Initialization and injection of buckets");
    }

    private void injectBucketIntoRepositoryClass(Class clazz, Bucket bucket) {
        for(Field field : clazz.getDeclaredFields()) {
            if(field.isAnnotationPresent(OFSRepository.class)) {
                try {
                    field.set(clazz, bucket);
                } catch (IllegalAccessException e) {
                    throw new ServerException(HttpStatus.SERVICE_UNAVAILABLE);
                }
            }
        }
    }
}
