'use strict';
const moment = require('moment');
const winston = require('winston');
const RedisTransport = require('./redis-transport');

/** Configures a winston logger for test or production environments. */
class WinstonLoggerConfig {
    constructor(isTestEnvironment) {
        this._isTestEnv = isTestEnvironment;
        this._logger = null;
    }

    /** Configures and returns a winston logger. */
    configure() {
        if (!this._isTestEnv) {
            this._configureProdLogger();
        } else {
            this._configureTestLogger();
        }
        return this._logger;
    }

    _configureProdLogger() {
        this._logger = new winston.Logger({
            transports: [new RedisTransport()]
        });

        winston.handleExceptions(new RedisTransport());
    }

    /** Configures a logger for a test environment. */
    _configureTestLogger() {
        this._logger = new winston.Logger({
            transports: [
                new winston.transports.Console({
                    timestamp: this._getUnixTimestamp
                })
            ]
        });

        winston.handleExceptions(new winston.transports.Console({
            timestamp: this._getUnixTimestamp
        }));
    }

    /** Gets the current unix timestamp, which is in seconds */
    _getUnixTimestamp() {
        return moment().unix()
    }
}

module.exports = new WinstonLoggerConfig(process.env.NODE_ENV === 'test').configure();