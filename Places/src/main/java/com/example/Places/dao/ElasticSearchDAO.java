package com.example.Places.dao;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class ElasticSearchDAO {
    private static JestClient client;

    public ElasticSearchDAO() {
    }

    public void init(String server, String port) {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(server + ":" + port)
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(2) // a modifier
                .maxTotalConnection(10) // a modifier
                .build());
        client = factory.getObject();
        System.out.println("elasticSearch launched !");
    }

    public JestClient getClient() {
        return client;
    }
}
