'use strict';
const express = require('express');
const graphql = require('express-graphql');
const AuthMiddleware = require('./auth-middleware');
const logger = require('./logging/logger');
// Perform initial connection health check.
require('./data/database');

/** Encapsulates the service's HTTP server */
module.exports = class Server {
    /** Constructs a Server that will listen at the given port */
    constructor(port) {
        this._port = port;
        this._app = express();

        this._configureHealthCheck();
        // Always install the middleware after the health check.
        this._installMiddleware();
        this._configureGqlRoute();
    }

    /** Opens the server up to connections */
    listen() {
        this._app.listen(this._port);
        logger.info('server started');        
    }

    /** Configures an HTTP endpoint where clients can evaluate the service health */
    _configureHealthCheck() {
        this._app.use('/status', (req, res, next) => {
            // TODO: Check the health of the database connection.
            res.status(200).end();
        });
    }

    /** Installs middleware into the express chain */
    _installMiddleware() {
        // Conditionally authenticate requests based on test/production environment.
        if (process.env.NODE_ENV != 'test') {
            let auth = new AuthMiddleware(process.env.CLIENT_ID);
            this._app.use(auth.verifyGId.bind(auth));
        } else {
            let auth = new AuthMiddleware('test-client-id');
            this._app.use(async (req, res, next) => {
                const user = { email: 'janedoe@example.com', name: 'Jane Doe' };
                auth.storeClaims(req, user);
                await auth.storeAccount(req, user.email, user.name);
                next();
            });
        }
    }

    /** Configures a route where GraphQL requests can be sent */
    _configureGqlRoute() {
        this._app.use('/graphql', graphql({
            schema:    require('./gql-spec/schema.js').schema,
            rootValue: require('./gql-spec/resolver.js').root,
            graphiql:  process.env.NODE_ENV == 'test'
        }));
    }
}