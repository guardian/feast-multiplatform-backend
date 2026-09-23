package com.gu.recipe.backend.exceptions

/**
 * Exception thrown when a GraphQL response is missing expected data.
 */
class GraphQLMissingDataException :
    GraphQLRepositoryException("Response did not contain data")