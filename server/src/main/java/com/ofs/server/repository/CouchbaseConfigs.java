package com.ofs.server.repository;

import com.ofs.server.form.ValidationSchema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class CouchbaseConfigs {

    @Value("${clusterHostName}")
    private String clusterHostName;

    @Value("${bucketPassword}")
    private String bucketPassword;

    @Value("${clusterManagerUsername}")
    private String clusterManagerUsername;

    @Value("${clusterManagerPassword}")
    private String clusterManagerPassword;
}
