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
    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.username}")
    private String elasticUserName;

    @Value("${spring.elasticsearch.password}")
    private String elasticPassword;


    @Nonnull
    @Override
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(elasticsearchUri.replace("http://", ""));

        if (elasticsearchUri.startsWith("https://")) {
            builder.usingSsl();
        }

        if (!elasticUserName.isEmpty() && !elasticPassword.isEmpty()) {
            builder.withBasicAuth(elasticUserName, elasticPassword);
        }

        return builder.build();
    }

    private static SSLContext buildSSLContext(){
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()   ;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
