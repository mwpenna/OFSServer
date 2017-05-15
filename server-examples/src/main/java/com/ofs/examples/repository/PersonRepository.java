package com.ofs.examples.repository;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.couchbase.client.java.error.TemporaryFailureException;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.ofs.examples.model.Person;
import com.ofs.server.errors.NotFoundException;
import com.ofs.server.errors.ServiceUnavailableException;
import com.ofs.server.repository.BaseCouchbaseRepository;
import com.ofs.server.repository.ConnectionManager;
import com.ofs.server.repository.OFSRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@OFSRepository(value = "person")
public class PersonRepository extends BaseCouchbaseRepository<Person>{

    @Autowired
    ConnectionManager connectionManager;

    public void addPerson(Person person) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        Objects.requireNonNull(person);

        log.info("Attempting to add user with id: {}", person.getId());
        add(person.getId().toString(), connectionManager.getBucket("person"), person);
        log.info("User with id: {} has been added", person.getId());
    }

    public Optional<Person> getPersonById(String id) {
        log.info("Attempting to retreive user with id: {}", id);
        if(id == null) {
            log.warn("Id cannot be null");
            return Optional.empty();
        }

        return queryForObjectById(id, connectionManager.getBucket("person"), Person.class);
    }

    public Optional<Person> getPersonByName(String name) throws Exception{
        if(name == null) {
            return Optional.empty();
        }

        try {
            ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(
                    generateGetByNameQuery(), generateGetByNameParameters(name));
            return queryForObjectByParameters(query, connectionManager.getBucket("person"), Person.class);
        }
        catch (NoSuchElementException e) {
            log.info("No results returned for getPersonByName with name: {}", name);
            return Optional.empty();
        }
        catch (TemporaryFailureException e) {
            log.error("Temporary Failure with couchbase occured" , e);
            throw new ServiceUnavailableException();
        }
    }

    public void updatePerson(Person person) throws com.fasterxml.jackson.core.JsonProcessingException {
        Objects.requireNonNull(person);

        try {
            log.info("Attempting to update user with id: {}", person.getId());
            update(person.getId().toString(), connectionManager.getBucket("person"), person);
            log.info("user with id: {} has been updated", person.getId());
        }
        catch(DocumentDoesNotExistException e) {
            log.warn("User with id: {} was not found", person.getId());
            throw new NotFoundException();
        }
        catch (TemporaryFailureException e) {
            log.error("Temporary Failure with couchbase occured" , e);
            throw new ServiceUnavailableException();
        }
    }

    public void deletePersonById(String id) {
        Objects.requireNonNull(id);

        try{
            log.info("Attempting to delete user with id: {}", id);
            delete(id, connectionManager.getBucket("person"));
            log.info("user with id: {} has been delete", id);
        }
        catch (DocumentDoesNotExistException e) {
            log.warn("User with id: {} was not found", id);
            throw new NotFoundException();
        }
        catch (TemporaryFailureException e) {
            log.error("Temporary Failure with couchbase occured" , e);
            throw new ServiceUnavailableException();
        }
    }

    private String generateGetByNameQuery() {
        return "SELECT `" + connectionManager.getBucket("person").name() + "`.* FROM `" + connectionManager.getBucket("person").name() + "` where name = $name";
    }

    private JsonObject generateGetByNameParameters(String name) {
        return JsonObject.create().put("$name", name);
    }
}
