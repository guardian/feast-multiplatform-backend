package com.gu.recipe.backend

import com.gu.recipe.backend.graphql.auth.AuthTokenProvider

/**
 * Updates the authorization token used by configured GraphQL HTTP interceptors.
 */
class FeastApiClient internal constructor(
    private val authTokenProvider: AuthTokenProvider,
) {
    var authToken: String?
        get() = authTokenProvider.authToken
        set(value) {
            authTokenProvider.updateAuthToken(value)
        }

    fun updateAuthToken(authToken: String?) {
        this.authToken = authToken
    }
}
