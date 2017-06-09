package com.ofs.server.advise;

import com.ofs.server.OFSController;
import com.ofs.server.errors.BadRequestException;
import com.ofs.server.errors.ConflictException;
import com.ofs.server.errors.ForbiddenException;
import com.ofs.server.errors.NotFoundException;
import com.ofs.server.errors.PreconditionFailedException;
import com.ofs.server.errors.ServiceUnavailableException;
import com.ofs.server.errors.TimeoutException;
import com.ofs.server.errors.UnauthorizedException;
import com.ofs.server.errors.UnsupportedMediaTypeException;
import com.ofs.server.model.OFSError;
import com.ofs.server.model.OFSErrors;
import com.ofs.server.utils.MapMessageFormat;
import com.ofs.server.utils.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.lang.String.format;

@ControllerAdvice(annotations = OFSController.class)
public class OFSErrorAdvice {

    @Value("${ERROR_BUNDLE_NAME:OFSServerErrors}")
    private String bundleName;

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity handleUnexpectedException(Throwable thr)
    {
        thr.printStackTrace();
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = SecurityException.class)
    public ResponseEntity handleSecurityException()
    {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity handleForbidden()
    {
        HttpHeaders headers = createHeaders("Access-Control-Allow-Origin", "*");
        return new ResponseEntity(headers, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity handleNotFound()
    {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = TimeoutException.class)
    public ResponseEntity handleTimeout()
    {
        return new ResponseEntity(HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(value = UnsupportedMediaTypeException.class)
    public ResponseEntity handleUnsupportedMediaType()
    {
        return new ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(value = PreconditionFailedException.class)
    public ResponseEntity handlePreconditionFailed()
    {
        return new ResponseEntity(HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public ResponseEntity handleMediaTypeNotSupported()
    {
        return new ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }



    // Error Body Error Responses

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity handleBadRequest(HttpServletRequest request, BadRequestException e)
            throws NoSuchFieldException
    {
        HttpHeaders headers = createHeaders();
        try {
            return new ResponseEntity<>(localize(e.getErrors(), request.getLocale()), headers, HttpStatus.BAD_REQUEST);
        } catch(MissingResourceException mre) {
            return handleUnexpectedException(mre);
        }
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity handleUnauthorized(HttpServletRequest request, UnauthorizedException e)
            throws NoSuchFieldException
    {
        HttpHeaders headers = createHeaders();
        headers.add(HttpHeaders.WWW_AUTHENTICATE, format("%s realm=\"%s\"", e.getScheme(), e.getRealm()));
        try {
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        } catch(MissingResourceException mre) {
            return handleUnexpectedException(mre);
        }
    }


    @ExceptionHandler(value = ConflictException.class)
    public ResponseEntity handleConflict(HttpServletRequest request, ConflictException e)
            throws NoSuchFieldException
    {
        HttpHeaders headers = createHeaders("Location", Objects.toString(e.getResource()));
        try {
            return new ResponseEntity<>(localize(e.getErrors(), request.getLocale()), headers, HttpStatus.CONFLICT);
        } catch(MissingResourceException mre) {
            return handleUnexpectedException(mre);
        }
    }

    @ExceptionHandler(value = ServiceUnavailableException.class)
    public ResponseEntity handleServiceUnavailable(HttpServletRequest request, ServiceUnavailableException e)
            throws NoSuchFieldException
    {
        try {
            if(e.getRetryAfter() > 0) {
                HttpHeaders headers = createHeaders("Retry-After", Integer.toString(e.getRetryAfter()));
                return new ResponseEntity<>(localize(e.getErrors(), request.getLocale()), headers, HttpStatus.SERVICE_UNAVAILABLE);
            }
            return new ResponseEntity<>(localize(e.getErrors(), request.getLocale()), HttpStatus.SERVICE_UNAVAILABLE);
        } catch(MissingResourceException mre) {
            return handleUnexpectedException(mre);
        }
    }

    private HttpHeaders createHeaders(String name, String value)
    {
        HttpHeaders headers = createHeaders();
        headers.set(name, value);
        return headers;
    }

    private HttpHeaders createHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return headers;
    }

// utility methods to create localized error bodies

    private OFSErrors localize(OFSErrors errors, Locale locale)
            throws NoSuchFieldException
    {
        if(errors != null) {
            Field messageField = OFSError.class.getDeclaredField("message");
            messageField.setAccessible(true);
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            for(OFSError error : errors) {
                if(ReflectionUtils.getField(messageField, error) == null) {
                    MapMessageFormat format = new MapMessageFormat(getResource(bundle, error.getCode()), locale);
                    ReflectionUtils.setField(messageField, error, format.format(error.getProperties()));
                }
            }
        }
        return errors;
    }

    private String getResource(ResourceBundle bundle, String code)
    {
        String[] parts = code.split("\\.");
        for(int i = 0; i < parts.length; i++) {
            try {
                String key = Strings.join(".", parts, i, parts.length - i);
                return bundle.getString(key);
            } catch(MissingResourceException e) { /* Silently ignore */ }
        }
        throw new MissingResourceException(
                format("Can't find resource %s for bundle %s",
                        code, bundle.getClass().getName()),
                bundle.getClass().getName(), code);
    }
}
