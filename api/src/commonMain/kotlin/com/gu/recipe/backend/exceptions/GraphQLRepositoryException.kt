package com.gu.recipe.backend.exceptions

import com.gu.recipe.backend.graphql.GraphQLError
import kotlin.coroutines.cancellation.CancellationException

/**
 * Exception thrown by GraphQL repository operations.
 *
 * @param message The detail message.
 * @param cause The cause of this exception.
 */
@RepositoryException
open class GraphQLRepositoryException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

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

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class RepositoryException