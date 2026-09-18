package com.gu.recipe.backend.exceptions

class GraphQLTransportException(
    cause: Throwable,
) : GraphQLRepositoryException("GraphQL transport request failed", cause)