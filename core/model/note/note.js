'use strict';
const { db } = require('../../service/database');

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
        return await db('note_tags')
            .select('tags.label')
            .join('tags', 'note_tags.tag_id', 'tags.id')
            .where({ note_id: this.id })
            .map(row => { return row.label; });
    }

    /**
     * Resolves the watchers field
     * @returns {Promise.<Array.<NoteWatcher>>}
     */
    async watchers() {
        // TODO: Note#watchers()
    }
};