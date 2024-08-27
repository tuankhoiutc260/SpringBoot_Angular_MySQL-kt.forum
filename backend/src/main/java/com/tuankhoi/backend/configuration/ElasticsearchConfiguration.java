package com.tuankhoi.backend.configuration;

import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.tuankhoi.backend.repository.Elasticsearch")
public class ElasticsearchConfiguration extends org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration {
    @Value("${elastic.userName}")
    private String elasticUserName;

    @Value("${elastic.password}")
    private String elasticPassword;

    @Nonnull
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedToLocalhost()
                .usingSsl(buildSSLContext())
                .withBasicAuth(elasticUserName, elasticPassword)
                .build();
    }

    private static SSLContext buildSSLContext(){
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()   ;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
