'use strict';
const express = require('express');
const AuthMiddleware = require('./service/auth-middleware');
// Perform initial connection health check.
require('./service/database');

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
    app.use((req, res, next) => {
        auth.storeClaims(req, {
            email: 'test',
            name: 'Jane Doe'
        })
        next();
    });
}

app.listen(8080);
console.log('server started');