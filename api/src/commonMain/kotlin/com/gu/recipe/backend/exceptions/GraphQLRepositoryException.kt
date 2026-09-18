package com.gu.recipe.backend.exceptions

open class GraphQLRepositoryException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)