package com.gu.recipe.backend

import com.gu.recipe.backend.exceptions.GraphQLResponseException
import kotlinx.coroutines.await
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FeastGraphQLTest {
    @Test
    fun frontsAreSerializedAsGraphQlJson() = runTest {
        withGraphQlResponse("""{"data":{"Front":[{"title":"Web","items":[]}]}}""") { api ->
            assertEquals(
                """[{"title":"Web","items":[]}]""",
                api.getFrontByRegion("northern", "all", 2).await(),
            )
        }
    }

    @Test
    fun missingContainersAreSerializedAsJsonNull() = runTest {
        withGraphQlResponse("""{"data":{"Container":null,"curatedContainerById":null}}""") { api ->
            assertEquals("null", api.getDishOfTheDayContainer("northern", "all").await())
            assertEquals("null", api.getCuratedCollection("123e4567-e89b-12d3-a456-426614174000").await())
        }
    }

    @Test
    fun graphQlErrorsRejectPromises() = runTest {
        withGraphQlResponse("""{"errors":[{"message":"Not authorised"}]}""") { api ->
            val exception = assertFailsWith<GraphQLResponseException> {
                api.getFrontByRegion("northern", "all", 2).await()
            }
            assertEquals("Not authorised", exception.message)
        }
    }

    @Test
    fun invalidCollectionIdRejectsPromise() = runTest {
        val api = FeastGraphQL("https://example.invalid")
        try {
            val exception = assertFailsWith<IllegalArgumentException> {
                api.getCuratedCollection("not-a-uuid").await()
            }
            assertEquals("Invalid collectionId UUID: not-a-uuid", exception.message)
        } finally {
            api.close()
        }
    }

    @Test
    fun unknownRegionRejectsPromise() = runTest {
        val api = FeastGraphQL("https://example.invalid")
        try {
            val exception = assertFailsWith<IllegalArgumentException> {
                api.getFrontByRegion("unknown", "all", 2).await()
            }
            assertEquals("Unknown region: unknown", exception.message)
        } finally {
            api.close()
        }
    }

    @Test
    fun unknownEditionRejectsPromise() = runTest {
        val api = FeastGraphQL("https://example.invalid")
        try {
            val exception = assertFailsWith<IllegalArgumentException> {
                api.getDishOfTheDayContainer("northern", "unknown").await()
            }
            assertEquals("Unknown edition: unknown", exception.message)
        } finally {
            api.close()
        }
    }

    @Test
    fun closedClientRejectsNewRequests() = runTest {
        val api = FeastGraphQL("https://example.invalid")
        api.close()

        assertFailsWith<CancellationException> {
            api.getFrontByRegion("northern", "all", 2).await()
        }
    }
}

private suspend fun withGraphQlResponse(response: String, block: suspend (FeastGraphQL) -> Unit) {
    val global = js("globalThis")
    val originalFetch = global.fetch
    global.fetch = { _: dynamic, _: dynamic ->
        Promise.resolve<dynamic>(
            js("new Response(response, { headers: { 'Content-Type': 'application/json' } })"),
        )
    }
    val api = FeastGraphQL("https://example.invalid")
    try {
        block(api)
    } finally {
        api.close()
        global.fetch = originalFetch
    }
}