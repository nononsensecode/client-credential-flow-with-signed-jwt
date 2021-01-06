package com.nononsensecode.oauth.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AccessTokenDTO(
    @JsonProperty(value = "access_token")
    val token: String,

    @JsonProperty(value = "expires_in")
    val expiresIn: Int,

    @JsonProperty(value = "refresh_expires_in")
    val refreshExpiresIn: Int,

    @JsonProperty(value = "refresh_token")
    val refreshToken: String,

    @JsonProperty(value = "token_type")
    val tokenType: String,

    @JsonProperty(value = "not_before_policy")
    val notBeforePolicy: Int,

    @JsonProperty(value = "session_state")
    val sessionState: String,

    @JsonProperty(value = "scope")
    val scope: String
)