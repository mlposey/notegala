'use strict';
const { db } = require('../data/database');
const Note = require('../note/note');
const NoteRepository = require('../note/repo/note-repository');
const NoteNbSpec = require('../note/repo/notebook-spec');

/**
 * Models a labeled collection of related notes
 * 
 * This class implements the Notebook GraphQL type.
 */
module.exports = class Notebook {
    /**
     * Instantiates a new Notebook object
     * 
     * @param {number} owner The id of the notebook owner
     * @param {string} title The notebook name
     * @param {Object} options Optional data that may define:
     *                 id        - The unique id of the notebook
     *                 createdAt - timestamp indicating creation
     */
    constructor(owner, title, options = {}) {
        this.owner = owner;
        this.title = title;
        this.id = options.id;
        this.createdAt = options.createdAt;
    }

    /** @returns {Promise.<Array.<Note>>} All notes in the notebook */
    async notes() {
        const repo = new NoteRepository();
        return await repo
            .find(new NoteNbSpec(this.id));
    }

    /**
     * Adds the note to the notebook
     * @param {Note} note 
     */
    async addNote(note) {
        await db('notebook_notes').insert({
            notebook_id: this.id,
            note_id: note.id
        });
    }

    /**
     * Removes the note from the notebook
     * @param {Note} note 
     */
    async removeNote(note) {
        await db('notebook_notes').where({
            notebook_id: this.id,
            note_id: note.id
        }).del();
    }
};