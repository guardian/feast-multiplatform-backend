package com.gu.recipe.backend.repository

import com.gu.recipe.backend.exceptions.*
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
     * @throws GraphQLResponseException when the GraphQL response is invalid.
     * @throws GraphQLMissingDataException when required data is missing from the response.
     * @throws GraphQLTransportException when the transport layer fails.
     * @throws GraphQLUnexpectedException when an unexpected error occurs.
     * @throws CancellationException when the coroutine is cancelled.
     */
    @Throws(
        GraphQLResponseException::class,
        GraphQLMissingDataException::class,
        GraphQLTransportException::class,
        GraphQLUnexpectedException::class,
        CancellationException::class,
    )
    suspend fun getFrontByRegion(
        region: Regions,
        edition: Editions,
        recipesLimit: Int,
    ): List<GetFrontsByRegionQuery.Front>

    /**
     * Fetches the dish of the day container for a specific region and edition.
     *
     * @param region the target region.
     * @param edition the target edition.
     * @return the dish of the day container, or null if not available.
     * @throws GraphQLResponseException when the GraphQL response is invalid.
     * @throws GraphQLMissingDataException when required data is missing from the response.
     * @throws GraphQLTransportException when the transport layer fails.
     * @throws GraphQLUnexpectedException when an unexpected error occurs.
     * @throws CancellationException when the coroutine is cancelled.
     */
    @Throws(
        GraphQLResponseException::class,
        GraphQLMissingDataException::class,
        GraphQLTransportException::class,
        GraphQLUnexpectedException::class,
        CancellationException::class,
    )
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
     * @throws GraphQLResponseException when the GraphQL response is invalid.
     * @throws GraphQLMissingDataException when required data is missing from the response.
     * @throws GraphQLTransportException when the transport layer fails.
     * @throws GraphQLUnexpectedException when an unexpected error occurs.
     * @throws CancellationException when the coroutine is cancelled.
     * @throws IllegalArgumentException when [collectionId] is not a UUID.
     */
    @Throws(
        GraphQLResponseException::class,
        GraphQLMissingDataException::class,
        GraphQLTransportException::class,
        GraphQLUnexpectedException::class,
        CancellationException::class,
        IllegalArgumentException::class,
    )
    suspend fun getCuratedCollection(
        collectionId: String,
    ): CuratedContainerByIdQuery.CuratedContainerById?
}
