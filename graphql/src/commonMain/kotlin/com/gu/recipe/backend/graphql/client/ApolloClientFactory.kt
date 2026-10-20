package com.gu.recipe.backend.graphql.client

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.gu.recipe.backend.graphql.auth.AuthTokenProvider
import com.gu.recipe.backend.graphql.auth.AuthorizationHttpInterceptor
import com.gu.recipe.backend.graphql.config.GraphQlConfig
import kotlinx.coroutines.CoroutineDispatcher

class ApolloClientFactory(
    private val dispatcher: CoroutineDispatcher,
    authTokenProvider: AuthTokenProvider,
) {
    private val authorizationHttpInterceptor = AuthorizationHttpInterceptor(authTokenProvider)

    @OptIn(ApolloExperimental::class)
    fun create(
        config: GraphQlConfig,
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(config.serverUrl)
            .dispatcher(dispatcher)
            .addHttpInterceptor(authorizationHttpInterceptor)
            .failFastIfOffline(true)
            .build()
    }
}