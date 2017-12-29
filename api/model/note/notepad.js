'use strict';
const { db } = require('../../service/database');
const Account = require('../account.js');

/** Indicates the email is unrecognized */
class EmailError extends Error {
    constructor() {
        super('unrecognized email');
    }
}

/** Indicates the necessary permissions are missing */
class PermissionError extends Error {
    constructor() {
        super('missing required permissions');
    }
}

/**
 * Handles modification of notes
 * 
 * Notepad ensures that the local and database representations
 * remain in sync and that note permissions are respected.
 */
class Notepad {
    /**
     * Constructs a Notepad where a note can be edited
     * 
     * @param {Note} note 
     * @param {Account} editor 
     */
    constructor(note, editor) {
        this.note = note;
        this.editor = editor;
    }

    /**
     * Builds a Notepad where an account can edit a note
     * 
     * @param {Note} note The note to edit
     * @param {string} editorEmail The email of the user making the edit
     * @throws {EmailError} If the email is unrecognized
     * @return {Notepad}
     */
    static async build(note, editorEmail) {
        let account;
        try { account = await Account.fromEmail(editorEmail); }
        catch (e) { throw new EmailError(); }
        
        return new Notepad(note, account);
    }

    /**
     * Determines if the editor has permissions to modify the note
     * 
     * @return {Boolean} True if editing is possible; false otherwise
     */
    async canEdit() {
        if (this._canEdit == undefined) {
            let rows = await db('note_watchers')
                .select('id')
                .where({
                    user_id: this.editor.id,
                    note_id: this.note.id,
                    can_edit: true
                });
            this._canEdit = rows.length === 1;    
        }
        return this._canEdit;
    }

    /**
     * Adds a tag to the note if it is not already there
     * Tags are single words or phrases that aid in note categorization.
     * @param {string} tag
     * @throws {PermissionError} If the user is not allowed to make this change
     */
    async addTag(tag) {
        const canEdit = await this.canEdit();
        if (!canEdit) throw new PermissionError();

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
        `, [tag, this.note.id]);
    }

    /**
     * Replaces the old tag list with a new list
     * @param {Array.<string>} newList The new tag list. An empty array
     *                                 will clear all tags.
     * @throws {PermissionError} If the user is not allowed to make this change
     */
    async replaceTags(newList) {
        const canEdit = await this.canEdit();
        if (!canEdit) throw new PermissionError();

        const oldList = await this.note.tags();
        if (oldList.length == newList.length &&
            oldList.filter(tag => !newList.includes(tag)).length == 0) {
            return;
        }

        await db('note_tags')
            .where({note_id: this.note.id})
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
        const canEdit = await this.canEdit();
        if (!canEdit) throw new PermissionError();

        let payload = {};

        if (title) {
            payload.title = title === ' ' ? '' : title;
            this.note.title = payload.title;
        }
        if (body) {
            payload.body = body === ' ' ? '' : body;
            this.note.body = payload.body;
        }

        if (payload.title || payload.body) {
            await db('notes').update(payload).where({id: this.note.id});
        }

        if (tags) await this.replaceTags(tags);

        const rows = await db('notes')
            .select('last_modified')
            .where({id: this.note.id});
        // Database triggers may have changed the value.
        this.note.lastModified = rows[0].last_modified;
    }

    /**
     * Returns the number of spaces in text
     * @param {string} text 
     * @return {number}
     */
    countSpaces(text) {
        let count = 0;
        for (let c of text) {
            if (c === ' ') count++;
        }
        return count;
    }
}

module.exports = {
    EmailError: EmailError,
    PermissionError: PermissionError,
    Notepad: Notepad
};