package com.ofs.server.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.bucket.BucketType;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.error.BucketDoesNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ConnectionManager {

    @Autowired
    CouchbaseConfigs couchbaseConfigs;

    private CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
            .connectTimeout(TimeUnit.SECONDS.toMillis(10))
            .ioPoolSize(20)
            .build();

    private Cluster cluster;
    private Map<String, Bucket> bucketMap = new HashMap<>();

    public void disconnect() {
        cluster.disconnect();
    }

    private Cluster getCluster() {
        if(cluster == null) {
            cluster = CouchbaseCluster.create(env, couchbaseConfigs.getClusterHostName());
        }
        return cluster;
    }

    public Bucket getBucket(String bucketName) {
        if(bucketMap.containsKey(bucketName)) {
            return bucketMap.get(bucketName);
        }

        return openOrCreateBucket(bucketName);
    }

    private Bucket openOrCreateBucket(String bucketName) {
        try{
            return openConnection(bucketName);
        }
        catch (BucketDoesNotExistException exception) {
            log.warn("Bucket Does Not Exists. Attempting to create bucket");
            createNewBucket(bucketName);

            log.info("Bucket created. Attempting to open connection to bucket");
            return openConnection(bucketName);
        }
    }

    private void createNewBucket(String bucketName) {
        ClusterManager clusterManager = cluster.clusterManager("Administrator", "password");
        BucketSettings bucketSettings = new DefaultBucketSettings.Builder()
                .type(BucketType.COUCHBASE)
                .name(bucketName)
                .password("")
                .build();

        clusterManager.updateBucket(bucketSettings);
    }

    private Bucket openConnection(String bucketName) {
        Bucket bucket = getCluster().openBucket(bucketName, couchbaseConfigs.getClusterPassword());
        bucketMap.put(bucketName, bucket);
        return bucket;
    }
}
