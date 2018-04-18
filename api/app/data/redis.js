'use strict';
const logger = require('../logging/logger');
const redis = require('redis');
const redisAddr = process.env.REDIS_ADDR;

const client = redis.createClient({address: redisAddr});
client.on('error', (err) => {
    logger.error('redis error', {
        details: err,
        redisAddr: redisAddr
    });
});

module.exports = client;