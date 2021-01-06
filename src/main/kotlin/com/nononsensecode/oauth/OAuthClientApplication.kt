package com.nononsensecode.oauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OAuthClientApplication

fun main(args: Array<String>) {
    runApplication<OAuthClientApplication>(*args)
}