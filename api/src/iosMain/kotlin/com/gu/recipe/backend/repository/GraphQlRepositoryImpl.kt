package com.gu.recipe.backend.repository

import com.gu.recipe.backend.graphql.GraphQlResult
import com.gu.recipe.backend.graphql.GraphQLError
import com.gu.recipe.backend.graphql.generated.CuratedContainerByIdQuery
import com.gu.recipe.backend.graphql.generated.GetDishOfTheDayRecipeQuery
import com.gu.recipe.backend.graphql.generated.GetFrontsByRegionQuery
import com.gu.recipe.backend.graphql.generated.type.Editions
import com.gu.recipe.backend.graphql.generated.type.Regions
import com.gu.recipe.backend.graphql.repository.RecipeGraphQlDataSource

internal class GraphQlRepositoryImpl(
    private val dataSource: RecipeGraphQlDataSource,
) : GraphQLRepository {

    private fun makeThrowable(err: GraphQLError): Throwable {
        return when (err) {
            is GraphQLError.GraphQL -> Error(err.messages.joinToString("\n"))
            GraphQLError.MissingData -> Error("Missing data")
            is GraphQLError.Transport -> err.cause
            is GraphQLError.Unexpected -> err.cause
        }
    }

    override suspend fun getFrontByRegion(
        region: Regions,
        edition: Editions,
        recipesLimit: Int
    ): List<GetFrontsByRegionQuery.Front> {
        val result = dataSource.getFrontByRegion(
            region = region,
            edition = edition,
            recipesLimit = recipesLimit,
        )

        return when(result) {
            is GraphQlResult.Success -> result.value
            is GraphQlResult.Failure -> throw makeThrowable(result.error)
        }
    }

    override suspend fun getDishOfTheDayContainer(
        region: Regions,
        edition: Editions,
    ): GetDishOfTheDayRecipeQuery.Container? {
        val result = dataSource.getDishOfTheDayContainer(
            region = region,
            edition = edition,
        )
        return when(result) {
            is GraphQlResult.Success -> result.value
            is GraphQlResult.Failure -> throw makeThrowable(result.error)
        }
    }

    override suspend fun getCuratedCollection(
        collectionId: String
    ): CuratedContainerByIdQuery.CuratedContainerById? {
        if (!UUID_REGEX.matches(collectionId)) {
            throw IllegalArgumentException("Invalid collectionId UUID: $collectionId")
        }

        val result = dataSource.getCuratedCollection(
            collectionId = collectionId,
        )
        return when(result) {
            is GraphQlResult.Success -> result.value
            is GraphQlResult.Failure -> throw makeThrowable(result.error)
        }
    }

}

private val UUID_REGEX =
    Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
