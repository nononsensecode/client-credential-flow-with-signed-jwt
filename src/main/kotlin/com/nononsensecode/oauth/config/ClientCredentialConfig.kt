package com.nononsensecode.oauth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ConfigurationProperties(prefix = "oauth.client-credential-flow.signed-jwt")
@PropertySource("classpath:credentials.yaml", factory = YamlPropertySourceFactory::class)
class ClientCredentialConfig {
    lateinit var password: String

    fun passwordArray() = this.password.toCharArray()
}