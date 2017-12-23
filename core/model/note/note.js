'use strict';

/** Represents the Note GraphQL type */
module.exports = class Note {
    constructor(id, createdAt, lastModified, isPublic, title, body) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.isPublic = isPublic;
        this.title = title;
        this.body = body;
    }

    /**
     * Resolves the tags field
     * @returns {Promise.<Array.<string>>}
     */
    async tags() {
        // TODO: Note#tags()
    }

    /**
     * Resolves the watchers field
     * @returns {Promise.<Array.<NoteWatcher>>}
     */
    async watchers() {
        // TODO: Note#watchers()
    }
};