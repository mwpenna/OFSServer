package com.ofs.examples.controller.advanced;

import com.ofs.examples.service.PersonService;
import com.ofs.examples.model.Person;
import com.ofs.server.OFSController;
import com.ofs.server.OFSServerId;
import com.ofs.server.errors.NotFoundException;
import com.ofs.server.filter.views.Public;
import com.ofs.server.form.OFSServerForm;
import com.ofs.server.form.ValidationSchema;
import com.ofs.server.form.update.ChangeSet;
import com.ofs.server.model.OFSErrors;
import com.ofs.server.security.Authenticate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@OFSController(resolver = "PersonResolver", filter = Public.class)
@RequestMapping(value = "/advanced-person", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvancedPersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(value = "/healthcheck")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok().build();
    }

    @ValidationSchema(value = "/person-create.json")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@OFSServerId URI id, OFSServerForm<Person> form) throws Exception {
        Person person = form.create(id);
        person.setId(UUID.fromString(person.getIdFromHref()));
        personService.createPerson(person);
        return ResponseEntity.created(id).build();
    }

    @ValidationSchema(value = "/person-update.json")
    @PostMapping(value = "/id/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable String id, OFSServerForm<Person> form) throws Exception {
        Optional<Person> optionalPerson = personService.getPersonById(id);

        if(optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            ChangeSet changeSet = form.update(person);

            if(changeSet.size()>0) {
                personService.updatePerson(person);
            }

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value= "/id/{id}")
    @ResponseBody
    public Person getPersonById(@PathVariable("id") String id) throws Exception {
        Optional<Person> personOptional = personService.getPersonById(id);

        if(personOptional.isPresent()) {
            return personOptional.get();
        }
        else {
            throw new NotFoundException();
        }
    }
}
