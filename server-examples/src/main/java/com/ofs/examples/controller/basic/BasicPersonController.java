package com.ofs.examples.controller.basic;

import com.ofs.examples.service.PersonService;
import com.ofs.examples.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value="/basic-person", produces = MediaType.APPLICATION_JSON_VALUE)
public class BasicPersonController {

    @Autowired
    PersonService personService;

    @GetMapping(value = "/healthcheck")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(Person person) throws Exception{
        UUID id = UUID.randomUUID();
        person.setId(id);
        person.setHref(URI.create("http://localhost:8080/person/id/"+id));
        personService.createPerson(person);

        return ResponseEntity.created(URI.create("http://localhost:8080/person/id/"+id)).build();
    }
}
