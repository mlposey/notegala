'use strict';
const { db } = require('../../service/database');
const NoteWatcher = require('./note-watcher');

/** Represents the Note GraphQL type */
module.exports = class Note {
    constructor(id, ownerId, createdAt, lastModified, isPublic, title, body) {
        this.id = id;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.isPublic = isPublic;
        this.title = title;
        this.body = body;
    }

    /**
     * Adds a user to the note's watchers list
     * @param {string} email The email of the user to add
     * @param {boolean} canEdit Watchers with edit privileges can modify notes
     */
    async addWatcher(email, canEdit) {
        await db('note_watchers').insert({
            note_id: this.id,
            user_id: db('users').select('id').where({ email: email }),
            can_edit: canEdit
        });
    }

    /**
     * Adds a tag to the note if it is not already there
     * Tags are single words or phrases that aid in note categorization.
     * @param {string} tag 
     */
    async addTag(tag) {
        // Regarding the DO NOTHING on the note_tags insert:
        // note_tags has a unique constraint on (note_id, tag_id) in
        // order to prevent duplicate tags. Here, we can assume it's
        // okay to ignore a duplicate insert since the tag is there anyways.
        await db.raw(`
            WITH tag AS (
                INSERT INTO tags (label) VALUES (?)
                ON CONFLICT(label) DO UPDATE
                SET label=EXCLUDED.label
                RETURNING id
            )
            INSERT INTO note_tags (note_id, tag_id)
            VALUES (?, (SELECT id FROM tag))
            ON CONFLICT DO NOTHING
        `, [tag, this.id]);
    }

    /**
     * Replaces the old tag list with a new list
     * @param {Array.<string>} newList The new tag list. An empty array
     *                                 will clear all tags.
     */
    async replaceTags(newList) {
        const oldList = await this.tags();
        if (oldList.length == newList.length &&
            oldList.filter(tag => !newList.includes(tag)).length == 0) {
            return;
        }

        await db('note_tags')
            .where({note_id: this.id})
            .del();
        
        for (const tag of newList) {
            await this.addTag(tag);
        }
    }

    /**
     * Edits the contents of a note
     * 
     * @param {string} title The new title or null to keep the existing one     
     * @param {string} body The new body or null to keep the existing one
     * @param {Array.<string>} tags The new tags or null to keep the existing ones
     */
    async edit(title, body, tags) {
        if (title) {
            await db('notes').update({title: title}).where({id: this.id});
            this.title = title;
        }
        if (body) {
            await db('notes').update({body: body}).where({id: this.id});
            this.body = body;
        }
        if (tags) {
            await this.replaceTags(tags);
        }

        const rows = await db('notes')
            .select('last_modified')
            .where({id: this.id});
        // Database triggers should have changed the value.
        this.lastModified = rows[0].last_modified;
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
     * @returns {Promise.<Array.<NoteWatcher>>} This array is guaranteed to hold
     *                                          at least one watcher
     */
    async watchers() {
        return await db('note_watchers')
            .join('users', 'note_watchers.user_id', 'users.id')
            .select(['users.id', 'users.name', 'note_watchers.can_edit',
                     'note_watchers.since'])
            .where({ note_id: this.id })
            .map(row => {
                return new NoteWatcher(row.id, this.id, row.name, row.since,
                    row.can_edit);
            });
    }
};