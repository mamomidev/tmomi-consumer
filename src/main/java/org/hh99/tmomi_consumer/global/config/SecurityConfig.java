package org.hh99.tmomi_consumer.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Import({org.hh99.tmomi.global.config.SecurityConfig.class, org.hh99.tmomi.global.jwt.JwtTokenProvider.class})
public class SecurityConfig {

}
