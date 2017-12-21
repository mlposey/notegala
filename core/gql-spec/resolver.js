'use strict';
const { GraphQLError } = require('graphql');
const Account = require('../model/account');

// Root resolver for all GraphQL queries
module.exports.root = {
    account: (root, {email, name}, context) => {
        return Account.fromEmail(email)
            .then(acct => {
                return acct;
            })
            .catch(e => {
                return Account.construct(email, name);
            })
            .catch(e => {
                return new GraphQLError(e.message);
            })
    }
};