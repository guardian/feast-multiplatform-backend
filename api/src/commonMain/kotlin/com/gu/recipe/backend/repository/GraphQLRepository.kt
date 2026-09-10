package com.gu.recipe.backend.repository

import com.gu.recipe.backend.exceptions.GraphQLRepositoryException
import com.gu.recipe.backend.graphql.generated.CuratedContainerByIdQuery
import com.gu.recipe.backend.graphql.generated.GetDishOfTheDayRecipeQuery
import com.gu.recipe.backend.graphql.generated.GetFrontsByRegionQuery
import com.gu.recipe.backend.graphql.generated.type.Editions
import com.gu.recipe.backend.graphql.generated.type.Regions
import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository for GraphQL API retrieval operations.
 */
interface GraphQLRepository {
    /**
     * Fetches fronts for a specific region and edition.
     *
     * @param region the target region.
     * @param edition the target edition.
     * @param recipesLimit the maximum number of recipes to return.
     * @return the list of fronts.
     * @throws GraphQLRepositoryException when the request cannot be completed.
     */
    @Throws(GraphQLRepositoryException::class, CancellationException::class)
    suspend fun getFrontByRegion(
        region: Regions,
        edition: Editions,
        recipesLimit: Int,
    ): List<GetFrontsByRegionQuery.Front>

    @Throws(GraphQLRepositoryException::class, CancellationException::class)
    suspend fun getDishOfTheDayContainer(
        region: Regions,
        edition: Editions,
    ): GetDishOfTheDayRecipeQuery.Container?

    @Throws(GraphQLRepositoryException::class, CancellationException::class)
    suspend fun getCuratedCollection(
        collectionId: String,
    ): CuratedContainerByIdQuery.CuratedContainerById?
}
