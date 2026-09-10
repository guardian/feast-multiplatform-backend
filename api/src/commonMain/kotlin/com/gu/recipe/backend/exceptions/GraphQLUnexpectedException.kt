package com.gu.recipe.backend.exceptions

class GraphQLUnexpectedException(
    cause: Throwable,
) : GraphQLRepositoryException("Unexpected GraphQL error", cause)