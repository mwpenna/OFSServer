package com.ofs.examples.controller.advanced;

import com.ofs.examples.model.Family;
import com.ofs.examples.repository.FamilyRepository;
import com.ofs.server.OFSController;
import com.ofs.server.OFSServerId;
import com.ofs.server.form.OFSServerForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@OFSController
@RequestMapping(value = "/advanced-family", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdvancedFamilyController {

    @Autowired
    private FamilyRepository familyRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@OFSServerId URI id, OFSServerForm<Family> form) throws Exception {
        Family family = form.create(id);
        family.setId(UUID.fromString(family.getIdFromHref()));
        familyRepository.addFamily(family);
        return ResponseEntity.created(id).build();
    }

    @PostMapping(value="search")
    public List<Family> searchFamily(OFSServerForm<Family> form) throws Exception {
        log.debug("Fetching All Family Memebers");
        Optional<List<Family>> optionalFamilyList = familyRepository.getFamilyMembers();

        if(optionalFamilyList.isPresent()) {
            return form.search(optionalFamilyList.get());
        }
        else {
            return new ArrayList<>();
        }
    }
}
