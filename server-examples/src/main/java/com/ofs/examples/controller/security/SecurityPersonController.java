package com.ofs.examples.controller.security;

import com.ofs.examples.controller.service.PersonService;
import com.ofs.examples.model.Person;
import com.ofs.server.OFSController;
import com.ofs.server.OFSServerId;
import com.ofs.server.errors.UnauthorizedException;
import com.ofs.server.form.OFSServerForm;
import com.ofs.server.form.ValidationSchema;
import com.ofs.server.model.JWTSubject;
import com.ofs.server.security.Authenticate;
import com.ofs.server.security.SecurityContext;
import com.ofs.server.security.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.UUID;


/*If you want to manually test against this controller you will need user service up and running
  to authenticate against*/
@OFSController
@RequestMapping(value = "/security-person", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecurityPersonController {

    @Autowired
    private PersonService personService;

    @ValidationSchema(value = "/person-create.json")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Authenticate
    public ResponseEntity create(@OFSServerId URI id, OFSServerForm<Person> form) throws Exception {
        hasValidCreatePermisions();
        Person person = form.create(id);
        person.setId(UUID.fromString(person.getIdFromHref()));
        personService.createPerson(person);
        return ResponseEntity.created(id).build();
    }

    private void hasValidCreatePermisions() {
        Subject subject = SecurityContext.getSubject();
        if(!subject.getRole().equals(JWTSubject.Role.SYSTEM_ADMIN.toString())) {
            throw new UnauthorizedException("OAuth", "OFSServer");
        }
    }
}
