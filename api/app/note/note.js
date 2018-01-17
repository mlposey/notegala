'use strict';
const { db } = require('../data/database');
const NoteWatcher = require('./note-watcher');

/** Represents the Note GraphQL type */
module.exports = class Note {
    /**
     * Instantiates a new Note object
     * 
     * The note cannot have blank fields for both title and body.
     * @param {number} owner The id of the owner account
     * @param {string} title The note title
     * @param {string} body The content of the note
     * @param {Object} options Optional data that may define:
     *                 id           - The unique id of the notebook
     *                 createdAt    - Timestamp indicating creation
     *                 lastModified - Timestamp indicating creation
     *                 isPublic     - Determines if others can view the note
     *                                Defaults to false
     * @throws {Error} If the title and body are empty
     */
    constructor(owner, title, body, options = {}) {
        this.owner = owner;

        if (!title && !body) throw new Error('note is missing content');
        this.title = title;
        this.body = body;

        this.id = options.id;
        this.createdAt = options.createdAt;
        this.lastModified = options.lastModified;
        this.isPublic = options.isPublic || false;
    }

    /**
     * Adds a user to the note's watchers list
     * 
     * Private notes cannot have watchers added to them unless
     * the watcher is the note owner.
     * @param {number} userId The unique id of the user to add
     * @param {boolean} canEdit Watchers with edit privileges can modify notes
     */
    async addWatcher(userId, canEdit) {
        if (!this.isPublic && this.owner != userId) return;

        await db('note_watchers').insert({
            note_id: this.id,
            user_id: userId,
            can_edit: canEdit
        });
    }

    /**
     * Removes a watcher from the list
     * 
     * If the watcher was the owner, a new owner is assigned.
     * If this is the only watcher, the note is destroyed.
     * @param {number} userId The id of the user to remove
     */
    async removeWatcher(userId) {
        await db('note_watchers')
            .where({
                user_id: userId,
                note_id: this.id
            }).del();

        const watchers = await this.watchers();
        if (watchers.length === 0) {
            // No more watchers. Destroy it.
            await db('note_tags').where({ note_id: this.id }).del();
            await db('notes').where({ id: this.id }).del();
            return;
        }

        // Give the note a new owner.
        if (this.ownerId == userId) {
            const old = NoteWatcher.earliest(watchers);
            // TODO: This task could be turned into a database trigger.
            // I.e., On owner_id column update, set note_watcher can_edit = true
            if (!old.canEdit) await old.changeEditPerm(true);

            await db('notes')
                .update({ owner_id: old.id })
                .where({ id: this.id });
        }
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