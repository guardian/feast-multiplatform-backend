package com.gu.recipe.backend.repository

import com.gu.recipe.backend.graphql.GraphQLError
import com.gu.recipe.backend.graphql.GraphQlResult
import com.gu.recipe.backend.graphql.generated.CuratedContainerByIdQuery
import com.gu.recipe.backend.graphql.generated.GetDishOfTheDayRecipeQuery
import com.gu.recipe.backend.graphql.generated.GetFrontsByRegionQuery
import com.gu.recipe.backend.graphql.generated.type.Editions
import com.gu.recipe.backend.graphql.generated.type.Regions
import com.gu.recipe.backend.graphql.repository.RecipeGraphQlDataSource
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

class GraphQlRepositoryImplTest {

    @Test
    fun `getCuratedCollection returns nullable successful values`() = runTest {
        val dataSource = FakeRecipeGraphQlDataSource(
            curatedResult = GraphQlResult.Success(null),
        )
        val repository = GraphQlRepositoryImpl(dataSource)
        val collectionId = "123e4567-e89b-12d3-a456-426614174000"

        val result = repository.getCuratedCollection(collectionId)

        assertNull(result)
        assertEquals(collectionId, dataSource.capturedCollectionId)
    }

    @Test
    fun `getDishOfTheDayContainer returns nullable successful values`() = runTest {
        val repository = GraphQlRepositoryImpl(
            FakeRecipeGraphQlDataSource(
                dishOfTheDayResult = GraphQlResult.Success(null),
            ),
        )

        assertNull(repository.getDishOfTheDayContainer(Regions.northern, Editions.all))
    }

    @Test
    fun `getFrontByRegion throws a repository exception for GraphQL errors`() = runTest {
        val repository = GraphQlRepositoryImpl(
            FakeRecipeGraphQlDataSource(
                frontsResult = GraphQlResult.Failure(
                    GraphQLError.GraphQL(listOf("First error", "Second error")),
                ),
            ),
        )

        val exception = assertFailsWith<GraphQLRepositoryException> {
            repository.getFrontByRegion(Regions.northern, Editions.all, recipesLimit = 2)
        }

        assertEquals("First error\nSecond error", exception.message)
    }

    @Test
    fun `getFrontByRegion preserves transport failure causes`() = runTest {
        val cause = IllegalStateException("Network unavailable")
        val repository = GraphQlRepositoryImpl(
            FakeRecipeGraphQlDataSource(
                frontsResult = GraphQlResult.Failure(GraphQLError.Transport(cause)),
            ),
        )

        val exception = assertFailsWith<GraphQLRepositoryException> {
            repository.getFrontByRegion(Regions.northern, Editions.all, recipesLimit = 2)
        }

        assertEquals("GraphQL transport request failed", exception.message)
        assertSame(cause, exception.cause)
    }

    @Test
    fun `getFrontByRegion propagates cancellation`() = runTest {
        val cancellationException = CancellationException("Request cancelled")
        val repository = GraphQlRepositoryImpl(
            FakeRecipeGraphQlDataSource(
                frontsResult = GraphQlResult.Failure(
                    GraphQLError.Unexpected(cancellationException),
                ),
            ),
        )

        assertFailsWith<CancellationException> {
            repository.getFrontByRegion(Regions.northern, Editions.all, recipesLimit = 2)
        }
    }

    @Test
    fun `getCuratedCollection throws a repository exception for missing data`() = runTest {
        val repository = GraphQlRepositoryImpl(
            FakeRecipeGraphQlDataSource(
                curatedResult = GraphQlResult.Failure(GraphQLError.MissingData),
            ),
        )

        val exception = assertFailsWith<GraphQLRepositoryException> {
            repository.getCuratedCollection("123e4567-e89b-12d3-a456-426614174000")
        }

        assertEquals("Response did not contain data", exception.message)
    }

    @Test
    fun `getCuratedCollection validates collection IDs before loading data`() = runTest {
        val dataSource = FakeRecipeGraphQlDataSource()
        val repository = GraphQlRepositoryImpl(dataSource)

        val exception = assertFailsWith<GraphQLRepositoryException> {
            repository.getCuratedCollection("not-a-uuid")
        }

        assertEquals("Invalid collectionId UUID: not-a-uuid", exception.message)
        assertNull(dataSource.capturedCollectionId)
    }
}

private class FakeRecipeGraphQlDataSource(
    private val frontsResult: GraphQlResult<List<GetFrontsByRegionQuery.Front>> =
        GraphQlResult.Success(emptyList()),
    private val dishOfTheDayResult: GraphQlResult<GetDishOfTheDayRecipeQuery.Container?> =
        GraphQlResult.Success(null),
    private val curatedResult: GraphQlResult<CuratedContainerByIdQuery.CuratedContainerById?> =
        GraphQlResult.Success(null),
) : RecipeGraphQlDataSource {

    var capturedCollectionId: String? = null

    override suspend fun getFrontByRegion(
        region: Regions,
        edition: Editions,
        recipesLimit: Int,
    ): GraphQlResult<List<GetFrontsByRegionQuery.Front>> = frontsResult

    override suspend fun getDishOfTheDayContainer(
        region: Regions,
        edition: Editions,
    ): GraphQlResult<GetDishOfTheDayRecipeQuery.Container?> = dishOfTheDayResult

    override suspend fun getCuratedCollection(
        collectionId: String,
    ): GraphQlResult<CuratedContainerByIdQuery.CuratedContainerById?> {
        capturedCollectionId = collectionId
        return curatedResult
    }
}
