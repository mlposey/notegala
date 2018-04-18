'use strict';
const redis = require('../data/redis');
const moment = require('moment');
const Transport = require('winston-transport');

/** Transports logs to a Redis instance */
module.exports = class RedisTransport extends Transport {
    constructor(opts) {
        super(opts);
    }

    log(level, msg, meta, callback) {
        this._sendLog(level, msg, meta);
        this.emit('logged');
        callback();
    }

    /**
     * Sends a log to Redis
     * @param {string} level The log level
     * @param {string} msg The primary log message
     * @param {Object} meta Metadata regarding the log
     */
    _sendLog(level, msg, meta) {
        let payload = this._packageLogData(level, msg, meta);
        this._publish(level, payload);
    }

    /**
     * Packages log data into a single payload
     * @param {string} level The log level
     * @param {string} msg The primary log message
     * @param {Object} meta Metadata regarding the log
     * @returns {string}
     */
    _packageLogData(level, msg, meta) {
        return JSON.stringify({
            level: level,
            message: msg,
            timestamp: moment().unix(),
            context: meta
        });
    }

    /**
     * Publishes the log to a Redis channel
     * @param {string} level The log level
     * @param {string} payload The log data
     */
    _publish(level, payload) {
        redis.publish('logs.' + level, payload);
    }
};