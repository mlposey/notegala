'use strict';
const { buildSchema } = require('graphql');

// Schema for the GraphQL API
module.exports.schema = buildSchema(`
    type Query {
        # Retrieves the requester's account
        #
        # This query relies on the supplied bearer token
        # to uniquely identify an account. An account
        # will be created if one does not already exist.
        account: Account!
    }

    # Describes a user account
    type Account {
        # Unique id for the account
        id: ID!

        # Timestamp with timezone indicating sign up time
        createdAt: String!

        # Timestamp with timezone indicating last sign in
        lastSeen: String!

        # A unique email address
        email: String!

        # The display name others see when interacting with
        # this account -- not guaranteed to be unique
        name: String!
    }
`);