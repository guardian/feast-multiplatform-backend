package com.gu.recipe.backend.exceptions

/**
 * Exception thrown when a GraphQL response contains errors.
 *
 * @param messages List of error messages from the GraphQL response, joined with newlines
 */
class GraphQLResponseException(
    val messages: List<String>,
) : GraphQLRepositoryException(messages.joinToString("\n"))