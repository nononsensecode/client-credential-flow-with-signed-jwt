package com.nononsensecode.oauth.controller

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nononsensecode.oauth.dto.AccessTokenDTO
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.security.KeyStore
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.HashMap

@RestController
@RequestMapping("/api/heroes")
class HeroController(
    @Value("\${oauth2.token_url}")
    val tokenUrl: String,

    @Value("\${oauth2.grant_type}")
    val grantType: String,

    @Value("\${oauth2.client_id}")
    val clientId: String,

    @Value("\${oauth2.scope}")
    val scope: String,

    @Value("\${oauth2.client_assertion_type}")
    val clientAssertionType: String,

    @Value("\${oauth2.jwk.algorithm}")
    val jwkAlgorithm: String,

    @Value("\${oauth2.resource_server.url}")
    val heroApiUrl: String,

    val privateKey: KeyStore.PrivateKeyEntry
) {
    @GetMapping("/.well-known/jwks.json")
    fun getJwks(): ResponseEntity<MutableMap<String, Any>> {
        val builder = RSAKey.Builder(privateKey.certificate.publicKey as RSAPublicKey)
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID(clientId)
        val jwks = JWKSet(builder.build())
        return ResponseEntity(jwks.toJSONObject(), HttpStatus.OK)
    }

    @GetMapping
    fun getHeroes(): ResponseEntity<List<Hero>> {
        val signedJWT = buildJWT()
        val accessToken = getAccessToken(signedJWT)

        val restTemplate = RestTemplate()

        val heroHeader = HttpHeaders()
        heroHeader["content-type"] = "application/json"
        heroHeader["authorization"] = "Bearer ${accessToken.token}"

        val heroRequest = HttpEntity(null, heroHeader)

        return restTemplate.exchange(heroApiUrl, HttpMethod.GET, heroRequest, object : ParameterizedTypeReference<List<Hero>>() {})
    }

    private fun buildJWT(): String {
        val now = Instant.now()
        return Jwts.builder()
            .setAudience(tokenUrl)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
            .setIssuer(clientId)
            .setSubject(clientId)
            .setId(UUID.randomUUID().toString())
            .signWith(privateKey.privateKey)
            .compact()
    }

    private fun getAccessToken(signedJWT: String): AccessTokenDTO {
        val restTemplate = RestTemplate()

        val authHeader = HttpHeaders()
        authHeader.set("Content-Type", "application/x-www-form-urlencoded")

        val params = buildParams(signedJWT)
        val authRequest = HttpEntity(params, authHeader)
        val authResponse = restTemplate.postForEntity(tokenUrl, authRequest, AccessTokenDTO::class.java)

        return authResponse.body!!
    }

    private fun buildParams(signedJWT: String): LinkedMultiValueMap<String, String> {
        val params = LinkedMultiValueMap<String, String>()
        params["client_id"] = clientId
        params["scope"] = scope
        params["grant_type"] = grantType
        params["client_assertion_type"] = clientAssertionType
        params["client_assertion"] = signedJWT
        return params
    }

    private fun generateJWKSet(): Map<String, Any> {
        val rsa = privateKey.certificate.publicKey as RSAPublicKey
        val values: MutableMap<String, Any> = HashMap()
        values["kty"] = rsa.algorithm
        values["kid"] = UUID.randomUUID().toString()
        values["n"] = Base64.getUrlEncoder().encodeToString(rsa.modulus.toByteArray())
        values["e"] = Base64.getUrlEncoder().encodeToString(rsa.publicExponent.toByteArray())
        values["alg"] = jwkAlgorithm
        values["use"] = "sig"

        val keys: MutableMap<String, Any> = HashMap()
        keys["keys"] = arrayListOf(values)

        return keys
    }
}

data class Hero(
    val id: Int,
    val name: String
)