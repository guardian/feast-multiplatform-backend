package com.gu.recipe.backend.graphql.auth

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Holds the current authorization token used by GraphQL HTTP interceptors.
 *
 * A custom interceptor should read [authToken] while handling each request so token updates are
 * applied without rebuilding the Apollo client.
 */
class AuthTokenProvider(
    initialAuthToken: String? = null,
) {
    private val token = MutableStateFlow(initialAuthToken)

    val authToken: String?
        get() = token.value

    fun updateAuthToken(authToken: String?) {
        token.value = authToken
    }
}
