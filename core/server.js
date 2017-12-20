'use strict';
const express = require('express');

var app = express();
app.use('/status', (req, res, next) => {
    // TODO: Check the health of the database connection.
    res.status(200).end();
});

app.listen(8080);
console.log('server started');