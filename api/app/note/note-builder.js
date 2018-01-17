'use strict';
const Account = require('../account/account');
const Note = require('./note');
const NoteRepository = require('./note-repository');
const { Notepad } = require('./notepad');
const NotebookRepository = require('../notebook/notebook-repository');
const NotebookIdSpec = require('../notebook/id-spec');

/** Builds local and repository instances of Note */
module.exports = class NoteBuilder {
    /** @param {Account} owner The owner of the note being created */
    constructor(owner) {
        this.owner = owner;
        this.tags = [];
    }

    /**
     * Sets the optional title of the note
     * @param {string} title
     * @return {NoteBuilder}
     */
    setTitle(title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the optional body of the note
     * @param {string} body
     * @return {NoteBuilder}
     */
    setBody(body) {
        this.body = body;
        return this;
    }

    /**
     * Adds tags to a collection that will belong to the note
     * @param {Array.<string>} tags 
     * @return {NoteBuilder}
     */
    addTags(tags) {
        this.tags = this.tags.concat(tags);
        return this;
    }

    /**
     * Set the notebook that will hold the note
     * 
     * The notebook must belong to the account that is creating the note.
     * @param {number} id The notebook id
     * @return {NoteBuilder}
     */
    setNotebook(id) {
        this.addToNotebook = async (note) => {
            const notebookRepo = new NotebookRepository();
            const matches = await notebookRepo
                .find(new NotebookIdSpec(id));
            if (matches.length !== 0 && matches[0].owner === this.owner.id) {
                await matches[0].addNote(note);
            }
        }
        return this;
    }

    /**
     * Creates a new Note that exists locally and in NoteRepository
     * 
     * @throws {Error} If the owner account is invalid or does not own the
     *                 notebook to which the note is assigned
     * @throws {Error} If the note is missing both the title and body
     * @returns {Promise.<Note>}
     */
    async build() {
        const note = new Note(this.owner.id, this.title, this.body);
        const noteRepo = new NoteRepository();
        await noteRepo.add(note);

        // All notes are watched by at least their owner.
        await note.addWatcher(note.owner, true);

        const notepad = new Notepad(note, this.owner);
        for (let tag of this.tags) await notepad.addTag(tag);

        if (this.addToNotebook) await this.addToNotebook(note);
        return note;
    }
};