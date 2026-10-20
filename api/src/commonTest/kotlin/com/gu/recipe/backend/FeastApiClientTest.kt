package com.gu.recipe.backend

import com.gu.recipe.backend.graphql.auth.AuthTokenProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FeastApiClientTest {

    @Test
    fun `updates the shared GraphQL auth token`() {
        val authTokenProvider = AuthTokenProvider()
        val client = FeastApiClient(authTokenProvider)

        client.authToken = "first-token"
        client.updateAuthToken("second-token")

        assertEquals("second-token", authTokenProvider.authToken)
        assertEquals("second-token", client.authToken)
    }

    @Test
    fun `clears the shared GraphQL auth token`() {
        val authTokenProvider = AuthTokenProvider("token")
        val client = FeastApiClient(authTokenProvider)

        client.updateAuthToken(null)

        assertNull(authTokenProvider.authToken)
    }
}
