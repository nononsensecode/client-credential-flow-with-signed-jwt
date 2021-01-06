package com.nononsensecode.oauth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.KeyStore

@Configuration
class ApplicationConfig(
    val clientCredentialConfig: ClientCredentialConfig
) {
    @Bean
    fun keyStore(): KeyStore {
        val passwordArray = clientCredentialConfig.passwordArray()
        val keyStore = KeyStore.getInstance("JKS")
        keyStore.load(javaClass.getResourceAsStream("/keystore.jks"), passwordArray)
        return keyStore
    }

    @Bean
    fun getPrivateKey(keyStore: KeyStore): KeyStore.PrivateKeyEntry {
        val passwordArray = clientCredentialConfig.passwordArray()
        val entryPassword = KeyStore.PasswordProtection(passwordArray)
        return keyStore.getEntry("hero-app-client", entryPassword) as KeyStore.PrivateKeyEntry
    }
}