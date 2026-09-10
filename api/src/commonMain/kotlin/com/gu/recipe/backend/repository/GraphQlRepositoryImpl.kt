package com.gu.recipe.backend.repository

import com.gu.recipe.backend.exceptions.GraphQLRepositoryException
import com.gu.recipe.backend.exceptions.cancellationExceptionOrNull
import com.gu.recipe.backend.exceptions.toRepositoryException
import com.gu.recipe.backend.graphql.GraphQlResult
import com.gu.recipe.backend.graphql.generated.CuratedContainerByIdQuery
import com.gu.recipe.backend.graphql.generated.GetDishOfTheDayRecipeQuery
import com.gu.recipe.backend.graphql.generated.GetFrontsByRegionQuery
import com.gu.recipe.backend.graphql.generated.type.Editions
import com.gu.recipe.backend.graphql.generated.type.Regions
import com.gu.recipe.backend.graphql.repository.RecipeGraphQlDataSource

internal class GraphQlRepositoryImpl(
    private val dataSource: RecipeGraphQlDataSource,
) : GraphQLRepository {

    override suspend fun getFrontByRegion(
        region: Regions,
        edition: Editions,
        recipesLimit: Int,
    ): List<GetFrontsByRegionQuery.Front> =
        dataSource.getFrontByRegion(region, edition, recipesLimit).getOrThrow()

    override suspend fun getDishOfTheDayContainer(
        region: Regions,
        edition: Editions,
    ): GetDishOfTheDayRecipeQuery.Container? =
        dataSource.getDishOfTheDayContainer(region, edition).getOrThrow()

    override suspend fun getCuratedCollection(
        collectionId: String,
    ): CuratedContainerByIdQuery.CuratedContainerById? {
        if (!UUID_REGEX.matches(collectionId)) {
            throw GraphQLRepositoryException("Invalid collectionId UUID: $collectionId")
        }

        return dataSource.getCuratedCollection(collectionId).getOrThrow()
    }
}

private fun <T> GraphQlResult<T>.getOrThrow(): T = when (this) {
    is GraphQlResult.Success -> value
    is GraphQlResult.Failure -> {
        error.cancellationExceptionOrNull()?.let { throw it }
        throw error.toRepositoryException()
    }
}

private val UUID_REGEX = Regex(
    "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-"
            + "[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
)
