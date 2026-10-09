@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package com.gu.recipe.backend

import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.buildJsonString
import com.apollographql.apollo.api.list
import com.apollographql.apollo.api.nullable
import com.apollographql.apollo.api.obj
import com.gu.recipe.backend.graphql.client.ApolloClientFactory
import com.gu.recipe.backend.graphql.client.FeastGraphQlClient
import com.gu.recipe.backend.graphql.config.GraphQlConfig
import com.gu.recipe.backend.graphql.generated.adapter.CuratedContainerByIdQuery_ResponseAdapter
import com.gu.recipe.backend.graphql.generated.adapter.GetDishOfTheDayRecipeQuery_ResponseAdapter
import com.gu.recipe.backend.graphql.generated.adapter.GetFrontsByRegionQuery_ResponseAdapter
import com.gu.recipe.backend.graphql.generated.type.Editions
import com.gu.recipe.backend.graphql.generated.type.Regions
import com.gu.recipe.backend.graphql.repository.ApolloRecipeGraphQlDataSource
import com.gu.recipe.backend.repository.GraphQlRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.promise
import kotlin.js.Promise

@JsExport
class FeastGraphQL(baseUrl: String) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val client = ApolloClientFactory(Dispatchers.Default).create(GraphQlConfig(baseUrl))
    private val repository = GraphQlRepositoryImpl(
        ApolloRecipeGraphQlDataSource(FeastGraphQlClient(client)),
    )

    fun getFrontByRegion(region: String, edition: String, recipesLimit: Int): Promise<String> =
        scope.promise {
            val fronts = repository.getFrontByRegion(region.toRegion(), edition.toEdition(), recipesLimit)
            buildJsonString {
                GetFrontsByRegionQuery_ResponseAdapter.Front.obj().list()
                    .toJson(this, CustomScalarAdapters.Empty, fronts)
            }
        }

    fun getDishOfTheDayContainer(region: String, edition: String): Promise<String> =
        scope.promise {
            val container = repository.getDishOfTheDayContainer(region.toRegion(), edition.toEdition())
            buildJsonString {
                GetDishOfTheDayRecipeQuery_ResponseAdapter.Container.obj().nullable()
                    .toJson(this, CustomScalarAdapters.Empty, container)
            }
        }

    fun getCuratedCollection(collectionId: String): Promise<String> = scope.promise {
        val collection = repository.getCuratedCollection(collectionId)
        buildJsonString {
            CuratedContainerByIdQuery_ResponseAdapter.CuratedContainerById.obj().nullable()
                .toJson(this, CustomScalarAdapters.Empty, collection)
        }
    }

    fun close() {
        scope.cancel()
        client.close()
    }
}

private fun String.toRegion(): Regions = Regions.safeValueOf(this).also {
    require(it != Regions.UNKNOWN__) { "Unknown region: $this" }
}

private fun String.toEdition(): Editions = Editions.safeValueOf(this).also {
    require(it != Editions.UNKNOWN__) { "Unknown edition: $this" }
}