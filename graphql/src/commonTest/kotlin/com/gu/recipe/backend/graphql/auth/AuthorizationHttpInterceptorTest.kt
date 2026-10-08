package com.gu.recipe.backend.graphql.auth

import com.apollographql.apollo.api.http.HttpMethod
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptorChain
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthorizationHttpInterceptorTest {

    @Test
    fun `adds the current token as authorization header`() = runTest {
        val authTokenProvider = AuthTokenProvider("Bearer initial-token")
        val interceptor = AuthorizationHttpInterceptor(authTokenProvider)
        val chain = CapturingInterceptorChain()

        interceptor.intercept(request(), chain)
        authTokenProvider.updateAuthToken("Bearer updated-token")
        interceptor.intercept(request(), chain)

        assertEquals("Bearer updated-token", chain.request.headers.single().value)
        assertEquals("Authorization", chain.request.headers.single().name)
    }

    @Test
    fun `does not add authorization header when token is absent`() = runTest {
        val interceptor = AuthorizationHttpInterceptor(AuthTokenProvider())
        val chain = CapturingInterceptorChain()

        interceptor.intercept(request(), chain)

        assertNull(chain.request.headers.singleOrNull())
    }

    private fun request(): HttpRequest = HttpRequest.Builder(
        method = HttpMethod.Post,
        url = "https://recipes.guardianapis.com/graphql",
    ).build()
}

private class CapturingInterceptorChain : HttpInterceptorChain {
    lateinit var request: HttpRequest

    override suspend fun proceed(request: HttpRequest): HttpResponse {
        this.request = request
        return HttpResponse.Builder(statusCode = 200).build()
    }
}
