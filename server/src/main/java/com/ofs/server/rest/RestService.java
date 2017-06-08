package com.ofs.server.rest;

import com.ofs.server.errors.ClientException;
import com.ofs.server.errors.OFSException;
import com.ofs.server.errors.ServerException;
import com.ofs.server.errors.ServiceUnavailableException;
import com.ofs.server.errors.TimeoutException;
import com.ofs.server.utils.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.ofs.server.utils.Objects.isOneOf;
import static org.springframework.http.HttpStatus.*;

@Slf4j
public abstract class RestService {
    @Autowired
    private RestTemplate restTemplate;


    protected <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) {
        try {
            return restTemplate.exchange(requestEntity, responseType);
        } catch (TimeoutException te) {
            throw new ServiceUnavailableException();
        } catch (ServerException se) {
            throw processServerError(se, requestEntity);
        } catch (RestClientException rce) {
            throw processClientError(rce, requestEntity);
        }
    }

    protected <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        try {
            return restTemplate.exchange(requestEntity, responseType);
        } catch (TimeoutException te) {
            throw new ServiceUnavailableException();
        } catch (ClientException | ServerException se) {
            throw processServerError(se, requestEntity);
        } catch (RestClientException rce) {
            throw processClientError(rce, requestEntity);
        }
    }


    private RuntimeException processClientError(RestClientException rce, RequestEntity<?> requestEntity) {
        log.warn("Request to {} failed {}", requestEntity.getUrl(), getMessage(rce));
        return new TimeoutException();
    }

    private OFSException processServerError(OFSException se, RequestEntity<?> requestEntity) {
        if(isOneOf(se.getStatus(), SERVICE_UNAVAILABLE, REQUEST_TIMEOUT, INTERNAL_SERVER_ERROR, GATEWAY_TIMEOUT)) {
            return new ServiceUnavailableException();
        }
        return se;
    }

    private String getMessage(Throwable t) {
        return Throwables.getRootCause(t).getMessage();
    }
}
