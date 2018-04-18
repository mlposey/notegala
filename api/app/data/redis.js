'use strict';
const logger = require('../logging/logger');
const redis = require('redis');
const redisAddr = process.env.REDIS_ADDR;

const client = redis.createClient('redis://' + redisAddr);
client.on('error', (err) => {
    console.log(err);
});

module.exports = client;