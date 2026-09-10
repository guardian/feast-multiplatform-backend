package com.gu.recipe.backend.exceptions

import com.gu.recipe.backend.graphql.GraphQLError
import kotlin.coroutines.cancellation.CancellationException

class GraphQLMissingDataException :
    GraphQLRepositoryException("Response did not contain data")

internal fun GraphQLError.toRepositoryException(): GraphQLRepositoryException = when (this) {
    is GraphQLError.GraphQL -> GraphQLResponseException(messages)
    GraphQLError.MissingData -> GraphQLMissingDataException()
    is GraphQLError.Transport -> GraphQLTransportException(cause)
    is GraphQLError.Unexpected -> GraphQLUnexpectedException(cause)
}

internal fun GraphQLError.cancellationExceptionOrNull(): CancellationException? = when (this) {
    is GraphQLError.Transport -> cause as? CancellationException
    is GraphQLError.Unexpected -> cause as? CancellationException
    else -> null
}