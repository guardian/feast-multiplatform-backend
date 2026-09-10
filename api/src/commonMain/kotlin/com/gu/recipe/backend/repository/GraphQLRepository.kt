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

    /**
     * Fetches the specific fronts collection by ID.  It's expected that the
     * UUID has been provided externally, e.g. deep-link
     *
     * @param collectionId the UUID of the collection to fetch
     * @return CuratedContainerById.
     * @throws GraphQLRepositoryException when the request cannot be completed.
     */
    @Throws(GraphQLRepositoryException::class, CancellationException::class)
    @Throws(GraphQLRepositoryException::class, CancellationException::class)
    suspend fun getCuratedCollection(
        collectionId: String,
    ): CuratedContainerByIdQuery.CuratedContainerById?
}
