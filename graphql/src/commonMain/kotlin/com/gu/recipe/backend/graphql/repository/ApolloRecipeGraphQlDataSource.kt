package com.gu.recipe.backend.graphql.repository

import com.gu.recipe.backend.graphql.GraphQlResult
import com.gu.recipe.backend.graphql.client.FeastGraphQlClient
import com.gu.recipe.backend.graphql.generated.CuratedContainerByIdQuery
import com.gu.recipe.backend.graphql.generated.FaciaChefSearchQuery
import com.gu.recipe.backend.graphql.generated.GetDishOfTheDayRecipeQuery
import com.gu.recipe.backend.graphql.generated.GetFrontsByRegionQuery
import com.gu.recipe.backend.graphql.generated.GetRecipeBatchQuery
import com.gu.recipe.backend.graphql.generated.FaciaRecipeSearchQuery
import com.gu.recipe.backend.graphql.generated.type.SortableDates
import com.apollographql.apollo.api.Optional
import com.gu.recipe.backend.graphql.generated.type.Editions
import com.gu.recipe.backend.graphql.generated.type.Regions

class ApolloRecipeGraphQlDataSource(
    private val feastGraphQlClient: FeastGraphQlClient,
) : RecipeGraphQlDataSource {

    override suspend fun getFrontByRegion(
        region: Regions, edition: Editions, recipesLimit: Int
    ): GraphQlResult<List<GetFrontsByRegionQuery.Front>> {
        val result = feastGraphQlClient.query(
            GetFrontsByRegionQuery(
                region = region,
                edition = edition,
                recipesLimit2 = recipesLimit,
            ),
        )
        return if (result is GraphQlResult.Success) {
            GraphQlResult.Success(result.value.Front)
        } else {
            result as GraphQlResult.Failure
        }
    }

    override suspend fun getDishOfTheDayContainer(
        region: Regions,
        edition: Editions,
    ): GraphQlResult<GetDishOfTheDayRecipeQuery.Container?> {
        val result = feastGraphQlClient.query(
            GetDishOfTheDayRecipeQuery(
                region = region,
                edition = edition,
                alias = DISH_OF_THE_DAY_RECIPE_ALIAS
            )
        )
        return if (result is GraphQlResult.Success) {
            GraphQlResult.Success(result.value.Container)
        } else {
            result as GraphQlResult.Failure
        }
    }

    override suspend fun getCuratedCollection(
        collectionId: String
    ): GraphQlResult<CuratedContainerByIdQuery.CuratedContainerById?> {
        val result = feastGraphQlClient.query(
            CuratedContainerByIdQuery(
                collectionId = collectionId
            )
        )
        return if (result is GraphQlResult.Success) {
            GraphQlResult.Success(result.value.curatedContainerById)
        } else {
            result as GraphQlResult.Failure
        }
    }
    
    override suspend fun faciaRecipeSearch(
        query: String,
        celebrationId: String?,
        dietId: String?,
        uprateByDate: SortableDates?,
    ): GraphQlResult<FaciaRecipeSearchQuery.Search?> {
        val celebrationArg: Optional<List<String>?> =
            Optional.presentIfNotNull(celebrationId?.let(::listOf))
        val dietArg: Optional<List<String>?> =
            Optional.presentIfNotNull(dietId?.let(::listOf))
        val result = feastGraphQlClient.query(
            FaciaRecipeSearchQuery(
                query = query,
                celebrationId = celebrationArg,
                dietId = dietArg,
                uprateByDate = Optional.presentIfNotNull(uprateByDate),
            )
        )
        return if (result is GraphQlResult.Success) {
            GraphQlResult.Success(result.value.search)
        } else {
            result as GraphQlResult.Failure
        }
    }

    override suspend fun faciaChefSearch(
        limit: Int,
        q: String?,
        ids: Array<String>?
    ): GraphQlResult<List<FaciaChefSearchQuery.Chef>> {
        val result = feastGraphQlClient.query(
            FaciaChefSearchQuery(
                limit = limit,
                q = Optional.presentIfNotNull(q),
            )
        )
        return if (result is GraphQlResult.Success) {
            GraphQlResult.Success(result.value.chefs)
        } else {
            result as GraphQlResult.Failure
        }
    }

    override suspend fun getRecipeBatch(
        recipeIds: List<String>?,
    ): GraphQlResult<List<GetRecipeBatchQuery.Recipe>> {
        val result = feastGraphQlClient.query(
            GetRecipeBatchQuery(
                recipeIds = Optional.presentIfNotNull(recipeIds),
            )
        )
        return if (result is GraphQlResult.Success) {
            GraphQlResult.Success(result.value.recipes)
        } else {
            result as GraphQlResult.Failure
        }
    }
}

private const val DISH_OF_THE_DAY_RECIPE_ALIAS = "dotd"