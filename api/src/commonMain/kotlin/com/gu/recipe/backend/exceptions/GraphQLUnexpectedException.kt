package com.gu.recipe.backend.exceptions

/**
 * Exception thrown when an unexpected GraphQL error occurs.
 *
 * @param cause The underlying throwable that caused this exception
 */
class GraphQLUnexpectedException(
    cause: Throwable,
) : GraphQLRepositoryException("Unexpected GraphQL error", cause)