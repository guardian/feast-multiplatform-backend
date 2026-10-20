package com.gu.recipe.backend.graphql.auth

import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain

internal class AuthorizationHttpInterceptor(
    private val authTokenProvider: AuthTokenProvider,
) : HttpInterceptor {
    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain,
    ): HttpResponse {
        val authToken = authTokenProvider.authToken
        val authorizedRequest = if (authToken.isNullOrBlank()) {
            request
        } else {
            request.newBuilder()
                .addHeader(AUTHORIZATION_HEADER, authToken)
                .build()
        }

        return chain.proceed(authorizedRequest)
    }

    private companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
