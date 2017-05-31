package com.ofs.server.repository;

import com.couchbase.client.core.BackpressureException;
import com.couchbase.client.core.RequestCancelledException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.TemporaryFailureException;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ofs.server.errors.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
public abstract class BaseCouchbaseRepository<T> {

    @Autowired
    @Qualifier("ofsObjectMapper")
    protected com.fasterxml.jackson.databind.ObjectMapper ofsObjectMapper;

    public Optional<T> queryForObjectByParameters(ParameterizedN1qlQuery query, Bucket bucket, Class<T> clazz) throws Exception {

        Objects.requireNonNull(query);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(bucket);

        N1qlQueryResult queryResult = queryForObject(query, bucket);

        Map resultMap = null;

        for(N1qlQueryRow row : queryResult) {
            resultMap =  row.value().toMap();
        }

        return mapResultsToOptional(resultMap, clazz);
    }

    public Optional<T> queryForObjectById(String id, Bucket bucket, Class clazz) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(bucket);

        log.debug("Attempting to retrieve json document with id: {}", id);

        JsonDocument jsonDocument = queryForObject(id, bucket);

        if(jsonDocument == null || jsonDocument.content() == null) {
            log.info("JsonDocumnent not found with id: {}", id);
            return Optional.empty();
        }
        else {
            log.info("Attempting to map results for id {} to class {}", id, clazz.getName());
            return mapResultsToOptional(jsonDocument.content().toMap(), clazz);
        }
    }

    public void add(String id, Bucket bucket, Object object) throws JsonProcessingException {
        Objects.requireNonNull(id);
        Objects.requireNonNull(bucket);
        Objects.requireNonNull(object);

        JsonObject jsonObject = JsonObject.fromJson(ofsObjectMapper.writeValueAsString(object));
        JsonDocument jsonDocument = JsonDocument.create(id, jsonObject);

        bucket.insert(jsonDocument);
    }

    public void update(String id, Bucket bucket, Object object) throws JsonProcessingException {
        Objects.requireNonNull(id);
        Objects.requireNonNull(bucket);
        Objects.requireNonNull(object);

        JsonDocument userDocument = bucket.getAndLock(id, 5);
        JsonDocument updatedDocument = modifyJsonDocument(id, userDocument, object);

        bucket.replace(updatedDocument);
    }

    public void delete(String id, Bucket bucket) {
        Objects.requireNonNull(id);
        bucket.remove(id);
    }

    public Optional<List<T>> queryForObjectListByParameters(ParameterizedN1qlQuery query, Bucket bucket, Class<T> clazz) throws Exception {
        Objects.requireNonNull(query);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(bucket);
        N1qlQueryResult queryResult = queryForObject(query, bucket);

        List<T> userList = new ArrayList<>();

        N1qlQueryRow row;
        for(Iterator var6 = queryResult.iterator(); var6.hasNext();) {
            row = (N1qlQueryRow)var6.next();
            userList.add(mapResultsToObject(row.value().toMap(), clazz));
        }

        if(userList.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(userList);
        }
    }

    private JsonDocument modifyJsonDocument(String id, JsonDocument jsonDocument, Object object) throws com.fasterxml.jackson.core.JsonProcessingException {
        JsonObject jsonObject = JsonObject.fromJson(ofsObjectMapper.writeValueAsString(object));
        return JsonDocument.create(id, jsonObject, jsonDocument.cas());
    }

    private JsonDocument queryForObject(String id, Bucket bucket) {
        JsonDocument jsonDocument;

        try{
            jsonDocument = bucket.get(id);
        }
        catch (BackpressureException | RequestCancelledException |TemporaryFailureException ex) {
            log.error("Exception occured with connection to couchbase : {}", ex);
            throw new ServiceUnavailableException();
        }
        catch (RuntimeException ex) {
            log.error("Exception occured with connection to couchbase : {}", ex);
            throw new ServiceUnavailableException();
        }

        return jsonDocument;
    }

    private N1qlQueryResult queryForObject(ParameterizedN1qlQuery query, Bucket bucket) {
        N1qlQueryResult queryResult;

        try{
            queryResult = bucket.query(query);
        }
        catch (BackpressureException | RequestCancelledException ex) {
            log.error("Exception occured with connection to couchbase : {}", ex);
            throw new ServiceUnavailableException();
        }
        catch (NoSuchElementException ex) {
            throw ex;
        }
        catch (RuntimeException ex) {
            log.error("Exception occured with connection to couchbase : {}", ex);
            throw new ServiceUnavailableException();
        }

        return queryResult;
    }

    private Optional<T> mapResultsToOptional(Map resultMap, Class clazz) {
        T entity;

        try {
            if(resultMap == null || resultMap.isEmpty()) {
                return Optional.empty();
            }

            Constructor<T> constructor = clazz.getConstructor(Map.class);
            entity = constructor.newInstance(resultMap);
            return Optional.of(entity);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(format("%s is missing a map constructor", clazz.getTypeName()), e);
        }
        catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(format("Failed to create entity %s", clazz.getTypeName()), e);
        }
    }

    private T mapResultsToObject(Map resultMap, Class clazz) {
        try {
            if(resultMap != null && !resultMap.isEmpty()) {
                Constructor e = clazz.getConstructor(new Class[]{Map.class});
                Object entity = e.newInstance(new Object[]{resultMap});
                return (T) entity;
            } else {
                return null;
            }
        } catch (NoSuchMethodException var5) {
            throw new IllegalArgumentException(String.format("%s is missing a map constructor", new Object[]{clazz.getTypeName()}), var5);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException var6) {
            throw new RuntimeException(String.format("Failed to create entity %s", new Object[]{clazz.getTypeName()}), var6);
        }
    }
}
