package com.gu.recipe.backend.exceptions

class GraphQLResponseException(
    val messages: List<String>,
) : GraphQLRepositoryException(messages.joinToString("\n"))