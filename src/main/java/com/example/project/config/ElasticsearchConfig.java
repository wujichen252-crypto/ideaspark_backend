package com.example.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * Elasticsearch配置类
 * 配置Elasticsearch客户端连接
 */
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private String elasticsearchUris;
    
    @Value("${spring.elasticsearch.username:}")
    private String elasticsearchUsername;
    
    @Value("${spring.elasticsearch.password:}")
    private String elasticsearchPassword;
    
    /**
     * 配置Elasticsearch客户端
     *
     * @return ClientConfiguration
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUris.split(","))
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                .build();
    }
}
