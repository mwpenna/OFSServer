package com.ofs.examples.controller.respository;

import com.ofs.examples.model.Person;
import com.ofs.examples.repository.PersonRepository;
import com.ofs.server.OFSController;
import com.ofs.server.OFSServerId;
import com.ofs.server.form.OFSServerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.UUID;

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
}
