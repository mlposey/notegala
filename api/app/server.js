'use strict';
const express = require('express');
const graphql = require('express-graphql');
const AuthMiddleware = require('./auth-middleware');
const logger = require('./logging/logger');
// Perform initial connection health check.
require('./data/database');

var app = express();
app.use('/status', (req, res, next) => {
    // TODO: Check the health of the database connection.
    res.status(200).end();
});

// Conditionally verify requests based on test/production environment.
if (process.env.NODE_ENV != 'test') {
    let auth = new AuthMiddleware(process.env.CLIENT_ID);
    app.use(auth.verifyGId.bind(auth));
} else {
    let auth = new AuthMiddleware('test-client-id');    
    app.use(async (req, res, next) => {
        const user = {email: 'janedoe@example.com', name: 'Jane Doe'};
        auth.storeClaims(req, user);
        await auth.storeAccount(req, user.email, user.name);
        next();
    });
}

app.use('/graphql', graphql({
    schema:    require('./gql-spec/schema.js').schema,
    rootValue: require('./gql-spec/resolver.js').root,
    graphiql:  process.env.NODE_ENV == 'test'
}));

app.listen(8080);
logger.info('server started');