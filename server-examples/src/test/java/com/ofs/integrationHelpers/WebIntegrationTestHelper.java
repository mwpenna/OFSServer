package com.ofs.integrationHelpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofs.server.client.AuthenticationClient;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class WebIntegrationTestHelper {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    @Qualifier("ofsObjectMapper")
    public ObjectMapper ofsObjectMapper;

    @MockBean
    public AuthenticationClient authenticationClient;

    @Before
    public void superSetup() {

        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response)
                    throws IOException
            {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response)
                    throws IOException
            {

            }
        });
    }

    protected HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer " + token);
        return headers;
    }

    public String apiUrl(String path) {
        return "http://localhost:" + port + "/" + path;
    }
}
