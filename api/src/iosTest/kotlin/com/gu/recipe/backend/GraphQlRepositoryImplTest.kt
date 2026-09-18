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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class GraphQlRepositoryImplTest {

    @Test
    fun `getCuratedCollection returns a bare value when collection id is valid`() = runTest {
        val expected: CuratedContainerByIdQuery.CuratedContainerById? = null
        val dataSource = FakeRecipeGraphQlDataSource(curatedResult = GraphQlResult.Success(expected))
        val repository = GraphQlRepositoryImpl(dataSource)
        val collectionId = "123e4567-e89b-12d3-a456-426614174000"

        val actual = repository.getCuratedCollection(collectionId)

        assertEquals(collectionId, dataSource.capturedCollectionId)
        assertEquals(expected, actual)
    }

    @Test
    fun `getCuratedCollection throws when the data source returns a failure`() = runTest {
        val dataSource = FakeRecipeGraphQlDataSource(
            curatedResult = GraphQlResult.Failure(GraphQLError.MissingData)
        )
        val repository = GraphQlRepositoryImpl(dataSource)
        val collectionId = "123e4567-e89b-12d3-a456-426614174000"

        val error = assertFailsWith<Error> {
            repository.getCuratedCollection(collectionId)
        }

        assertEquals(collectionId, dataSource.capturedCollectionId)
        assertEquals("Missing data", error.message)
    }

    @Test
    fun `getCuratedCollection fails fast when collection id is invalid`() = runTest {
        val dataSource = FakeRecipeGraphQlDataSource(
            curatedResult = GraphQlResult.Failure(GraphQLError.MissingData)
        )
        val repository = GraphQlRepositoryImpl(dataSource)

        val error = assertFailsWith<IllegalArgumentException> {
            repository.getCuratedCollection("not-a-uuid")
        }

        assertNull(dataSource.capturedCollectionId)
        assertEquals("Invalid collectionId UUID: not-a-uuid", error.message)
    }
}

private class FakeRecipeGraphQlDataSource(
    private val curatedResult: GraphQlResult<CuratedContainerByIdQuery.CuratedContainerById?>,
) : RecipeGraphQlDataSource {

    var capturedCollectionId: String? = null

    override suspend fun getFrontByRegion(
        region: Regions,
        edition: Editions,
        recipesLimit: Int,
    ): GraphQlResult<List<GetFrontsByRegionQuery.Front>> =
        error("Not used in this test")

    override suspend fun getDishOfTheDayContainer(
        region: Regions,
        edition: Editions,
    ): GraphQlResult<GetDishOfTheDayRecipeQuery.Container?> =
        error("Not used in this test")

    override suspend fun getCuratedCollection(
        collectionId: String,
    ): GraphQlResult<CuratedContainerByIdQuery.CuratedContainerById?> {
        capturedCollectionId = collectionId
        return curatedResult
    }
}


