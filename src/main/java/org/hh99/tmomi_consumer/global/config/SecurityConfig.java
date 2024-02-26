package org.hh99.tmomi_consumer.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Import({org.hh99.tmomi.global.config.SecurityConfig.class, org.hh99.tmomi.global.jwt.JwtTokenProvider.class})
public class SecurityConfig {

}
