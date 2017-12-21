'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../model/account');

// Root resolver for all GraphQL queries
module.exports.root = {
    account: (root, {email, name}, context) => {
        // TODO: account query
        return new Account(0, '', '', 'placeholder@example.com', 'Placeholder');
    }
};