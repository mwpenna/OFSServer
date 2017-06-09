package com.ofs.server.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Charsets;
import com.ofs.server.errors.BadRequestException;
import com.ofs.server.errors.ClientException;
import com.ofs.server.errors.ConflictException;
import com.ofs.server.errors.ForbiddenException;
import com.ofs.server.errors.NotAcceptableException;
import com.ofs.server.errors.NotFoundException;
import com.ofs.server.errors.PreconditionFailedException;
import com.ofs.server.errors.ServerException;
import com.ofs.server.errors.ServiceUnavailableException;
import com.ofs.server.errors.TimeoutException;
import com.ofs.server.errors.UnauthorizedException;
import com.ofs.server.errors.UnsupportedMediaTypeException;
import com.ofs.server.model.OFSErrors;
import com.ofs.server.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ofs.server.utils.Objects.ifNull;

@Slf4j
public class OFSErrorHandler extends DefaultResponseErrorHandler {

    private static final Pattern REGEX = Pattern.compile("([\\w\\-]*)\\s{1}realm=\"([\\w\\-\\.@\\s]*)\"");

    @Autowired
    @Qualifier("ofsObjectMapper")
    private ObjectMapper mapper;

    private ObjectReader reader;


    @PostConstruct
    public void init()
    {
        reader = mapper.readerFor(OFSErrors.class);
    }

    /**
     * This default implementation throws a {@link HttpClientErrorException} if the response status code
     * is {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR}, a {@link HttpServerErrorException}
     * if it is {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR},
     * and a {@link RestClientException} in other cases.
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException
    {
        HttpStatus statusCode = getHttpStatusCode(response);
        HttpHeaders headers = response.getHeaders();
        switch(statusCode) {
            case BAD_REQUEST:
                throwBadRequestException(response);
            case UNAUTHORIZED:
                throwUnauthorizedException(headers.getFirst(HttpHeaders.WWW_AUTHENTICATE));
            case FORBIDDEN:
                throw new ForbiddenException();
            case NOT_FOUND:
                throw new NotFoundException();
            case REQUEST_TIMEOUT:
                log.info("Remote Service Timeout", new RuntimeException());
                throw new TimeoutException();
            case CONFLICT:
                throwConflictException(response, headers.getLocation());
            case PRECONDITION_FAILED:
                throw new PreconditionFailedException();
            case UNSUPPORTED_MEDIA_TYPE:
                throw new UnsupportedMediaTypeException();
            case NOT_ACCEPTABLE:
                throw new NotAcceptableException();
            case SERVICE_UNAVAILABLE:
                log.info("Service Unavailable", new RuntimeException());
                throwUnavailableException(response, headers.getFirst(HttpHeaders.RETRY_AFTER));
            default:
                switch (statusCode.series()) {
                    case CLIENT_ERROR:
                        throw new ClientException(statusCode);
                    case SERVER_ERROR:
                        throw new ServerException(statusCode);
                    default:
                        throw new RestClientException("Unknown status code [" + statusCode + "]");
                }
        }
    }

    void throwBadRequestException(ClientHttpResponse response) throws IOException
    {
        String body = getResponseBody(response).toString();
        OFSErrors errors = parseErrors(body);
        if(errors == null || errors.isEmpty()) {
            throw new BadRequestException(new OFSErrors()) {
                public String getMessage() {
                    return body;
                }
            };
        }
        throw new BadRequestException(errors);
    }

    void throwConflictException(ClientHttpResponse response, URI location) throws IOException
    {
        String body = getResponseBody(response).toString();
        OFSErrors errors = parseErrors(body);
        throw new ConflictException(location, errors);
    }

    void throwUnavailableException(ClientHttpResponse response, String header) throws IOException
    {
        String body = getResponseBody(response).toString();
        OFSErrors errors = parseErrors(body);
        throw new ServiceUnavailableException(errors, Integer.parseInt(header, 0));
    }

    void throwUnauthorizedException(String header)
    {
        if(header == null) {
            // TODO Work with Jarod to fix the 401 response to include WWW-Authenticate header
            throw new UnauthorizedException("Bearer", "api.manheim.com");
        } else {
            Matcher matcher = REGEX.matcher(header);
            if(matcher.find()) {
                throw new UnauthorizedException(matcher.group(1), matcher.group(2));
            }
        }
        throw new Error("Missing scheme/realm or my REGEX isn't working");
    }


    private OFSErrors parseErrors(String body) throws IOException
    {
        try {
            return reader.readValue(body);
        } catch(Exception e) {
            return null;
        }
    }

    protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        return response.getStatusCode();
    }

    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException var3) {
            return new byte[0];
        }
    }


    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null ? contentType.getCharSet() : null;
    }
}
