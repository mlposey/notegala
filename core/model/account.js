'use strict';

/** Represents the Account GraphQL type */
module.exports = class Account {
    constructor(id, createdAt, lastSeen, email, name) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastSeen = lastSeen;
        this.email = email;
        this.name = name;
    }
};