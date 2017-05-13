package com.ofs.server.repository;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CouchbaseConfigs {

    @Value("${clusterHostName}")
    private String clusterHostName;

    @Value("${clusterPassword}")
    private String clusterPassword;
}
