'use strict';
const { db } = require('../data/database');
const { Repository, NotFoundError } = require('../data/repository');
const Note = require('./note');

/** Manages the global collection of Notes */
module.exports = class NoteRepository extends Repository {
    /**
     * Adds a new note to the repository
     * @param {Note} note Will be updated to match the version as it
     *                    exists in the repository
     * @throws {Error} If note.owner is not a valid account id
     */
    async add(note) {
        const rows = await db('notes')
            .insert({
                owner_id: note.owner,
                title: note.title,
                body: note.body,
                is_public: note.isPublic
            })
            .returning(['id', 'created_at', 'last_modified'])
            .catch(err => { throw new Error('invalid owner id'); });
        
        note.id = rows[0].id;
        note.createdAt = rows[0].created_at;
        note.lastModified = rows[0].last_modified;
    }

    /**
     * Removes the note from the repository, detaching any tags
     * @param {Note} note 
     * @throws {NotFoundError} If the note is not in the repository
     */
    async remove(note) {
        await db('note_tags')
            .where({note_id: note.id})
            .del();
        const rows = await db('notes')
            .where({id: note.id})
            .del()
            .returning('id');
        
        if (rows.length === 0) throw new NotFoundError();
    }

    /**
     * Replaces the existing note in the repository
     * @param {Note} note An updated note
     *                    Note: The id must remain the same
     * @throws {NotFoundError} If the note is not in the repository
     */
    async replace(note) {
        const rows = await db('notes')
            .update({
                is_public: note.isPublic,
                title: note.title,
                body: note.body
            })
            .where({id: note.id})
            .returning('last_modified');

        if (rows.length === 0) throw new NotFoundError();
        note.lastModified = rows[0];
    }

    /**
     * Finds all notes in the repository that match the specification
     * @param {Specification} spec
     * @param {number} limit The maximum number of notes to retrieve
     * @return {Promise.<Array.<Note>>}
     */
    async find(spec, limit = this.DEFAULT_LIMIT) {
        let rows = await spec.toQuery().limit(limit);
        return rows.map(row => new Note(row.owner_id, row.title, row.body, {
            id: row.id,
            createdAt: row.created_at,
            lastModified: row.last_modified,
            isPublic: row.is_public
        }));
    }
};