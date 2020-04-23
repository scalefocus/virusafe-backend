package io.virusafe.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "io.virusafe.repository")
public class ElasticSearchConfiguration {

}
