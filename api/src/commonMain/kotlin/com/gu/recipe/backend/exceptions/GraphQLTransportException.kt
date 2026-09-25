package com.gu.recipe.backend.exceptions

/**
 * Exception thrown when a GraphQL transport request fails.
 *
 * @param cause The underlying cause of the transport failure.
 */
class GraphQLTransportException(
    cause: Throwable,
) : GraphQLRepositoryException("GraphQL transport request failed", cause)