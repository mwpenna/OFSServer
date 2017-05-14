package com.ofs.examples.repository;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.java.Bucket;
import com.ofs.examples.model.Person;
import com.ofs.server.repository.BaseCouchbaseRepository;
import com.ofs.server.repository.OFSRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class PersonRepository extends BaseCouchbaseRepository<Person>{

    @OFSRepository(value = "person")
    public Bucket bucket;

    public void addPerson(Person person) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        Objects.requireNonNull(person);

        log.info("Attempting to add user with id: {}", person.getId());
        add(person.getId().toString(), bucket, person);
        log.info("User with id: {} has been added", person.getId());
    }
}
