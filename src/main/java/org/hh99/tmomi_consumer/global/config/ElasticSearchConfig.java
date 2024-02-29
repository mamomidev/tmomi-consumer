package org.hh99.tmomi_consumer.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(org.hh99.tmomi.global.config.ElasticSearchConfig.class)
public class ElasticSearchConfig {

}
