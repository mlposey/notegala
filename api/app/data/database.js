'use strict';
const Knex = require('knex');
const logger = require('../logging/logger');

// The application database connection
var db = Knex({
    client: 'pg',
    connection: {
        host:     process.env.SQL_HOST,
        database: process.env.SQL_DATABASE,
        user:     process.env.SQL_USER,
        password: process.env.SQL_PASSWORD
    }
});

// Test the connection.
db.raw('select 1 + 1 as res')
    .then(() => {
        logger.info('connected to database');
    })
    .catch(e => {
        logger.error('could not connect to database');
        process.exit(1);
    });

module.exports.db = db;