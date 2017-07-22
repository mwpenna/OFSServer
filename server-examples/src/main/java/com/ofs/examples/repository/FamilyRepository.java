package com.ofs.examples.repository;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.TemporaryFailureException;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.ofs.examples.model.Family;
import com.ofs.server.errors.ServiceUnavailableException;
import com.ofs.server.repository.BaseCouchbaseRepository;
import com.ofs.server.repository.ConnectionManager;
import com.ofs.server.repository.OFSRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@OFSRepository(value = "family")
public class FamilyRepository extends BaseCouchbaseRepository<Family> {

    @Autowired
    ConnectionManager connectionManager;

    public void addFamily(Family family) throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        Objects.requireNonNull(family);

        log.info("Attempting to add family with id: {}", family.getId());
        add(family.getId().toString(), connectionManager.getBucket("family"), family);
        log.info("family with id: {} has been added", family.getId());
    }

    public Optional<List<Family>> getFamilyMembers() throws Exception {
        try {
            ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(
                    generateGetFamilyMembersQuery(), generateFamilyMemberParameters());
            return queryForObjectListByParameters(query, connectionManager.getBucket("family"), Family.class);
        }
        catch (NoSuchElementException e) {
            log.info("No results returned for getAllPersons");
            return Optional.empty();
        }
        catch (TemporaryFailureException e) {
            log.error("Temporary Failure with couchbase occured" , e);
            throw new ServiceUnavailableException();
        }
    }

    private String generateGetFamilyMembersQuery() {
        return "SELECT `" + connectionManager.getBucket("family").name() + "`.* FROM `" + connectionManager.getBucket("family").name()+"`";
    }

    private JsonObject generateFamilyMemberParameters() {
        return JsonObject.create();
    }
}
