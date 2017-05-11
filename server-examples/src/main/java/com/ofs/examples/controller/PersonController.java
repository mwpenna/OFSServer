package com.ofs.examples.controller;

import com.ofs.examples.model.Person;
import com.ofs.server.OFSServerId;
import com.ofs.server.form.OFSServerForm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value="/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {

    @GetMapping(value = "/healthcheck")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@OFSServerId URI id, OFSServerForm<Person> form) throws Exception{
        Person person = form.create(id);
        return ResponseEntity.created(id).build();
    }
}
