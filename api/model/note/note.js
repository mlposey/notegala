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
        // TODO: Make sure the email is the owner or the note is public.
        await db('note_watchers').insert({
            note_id: this.id,
            user_id: db('users').select('id').where({ email: email }),
            can_edit: canEdit
        });
    }

    /**
     * Removes the note from the user's collection
     * 
     * If the user indicated by the email was also the note
     * owner, the oldest watcher will become the new owner.
     * 
     * @returns {boolean} True if the process succeeds; false otherwise
     */
    async remove(email) {
        // Stop watching the note.
        const ids = await db('note_watchers')
            .where({
                user_id: db('users').select('id').where({email: email}),
                note_id: this.id
            }).del().returning('user_id');
        if (ids.length == 0) return false;

        const watchers = await this.watchers();
        // Completely delete it if they were the only watcher.
        if (watchers.length == 0) {
            await db('note_tags').where({note_id: this.id}).del();
            await db('notes').where({id: this.id}).del();
            return true;
        }

        // Give the note a new owner.
        if (this.ownerId == ids[0]) {
            const old = NoteWatcher.earliest(watchers);
            // TODO: This task could be turned into a database trigger.
            // I.e., On owner_id column update, set note_watcher can_edit = true
            if (!old.canEdit) await old.changeEditPerm(true);

            await db('notes')
                .update({owner_id: old.id})
                .where({id: this.id});
        }
        return true;
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