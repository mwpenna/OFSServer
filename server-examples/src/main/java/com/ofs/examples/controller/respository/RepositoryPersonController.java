package com.ofs.examples.controller.respository;

import com.ofs.examples.model.Person;
import com.ofs.examples.repository.PersonRepository;
import com.ofs.server.OFSController;
import com.ofs.server.OFSServerId;
import com.ofs.server.errors.NotFoundException;
import com.ofs.server.form.OFSServerForm;
import com.ofs.server.form.update.ChangeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@OFSController
@RequestMapping(value = "/repository-person", produces = MediaType.APPLICATION_JSON_VALUE)
public class RepositoryPersonController {

    @Autowired
    PersonRepository personRepository;

    @GetMapping(value = "/healthcheck")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@OFSServerId URI id, OFSServerForm<Person> form) throws Exception {
        Person person = form.create(id);
        person.setId(UUID.fromString(person.getIdFromHref()));
        personRepository.addPerson(person);
        return ResponseEntity.created(id).build();
    }

    @GetMapping(value= "/id/{id}")
    public Person getPersonById(@PathVariable("id") String id) throws Exception {
        log.debug("Fetching Person with id {}", id);
        Optional<Person> personOptional = personRepository.getPersonById(id);

        if(personOptional.isPresent()) {
            Person person = personOptional.get();
            log.debug("Person found with id {}", person.getId());

            return personOptional.get();
        }
        else {
            log.error("Person not found");
            throw new NotFoundException();
        }
    }

    @PostMapping(value = "/id/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable String id, OFSServerForm<Person> form) throws Exception {
        Optional<Person> personOptional = personRepository.getPersonById(id);

        if(personOptional.isPresent()) {
            Person person = personOptional.get();

            ChangeSet changeSet = form.update(person);

            if(changeSet.size()>0) {
                personRepository.updatePerson(person);
            }
            return ResponseEntity.noContent().build();
        }
        else {
            log.error("Person with id: {} not found", id);
            throw new NotFoundException();
        }
    }

    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity delete(@PathVariable("id") String id) throws Exception {
        personRepository.deletePersonById(id);
        return ResponseEntity.noContent().build();
    }
}