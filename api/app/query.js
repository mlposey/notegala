'use strict';
const { db } = require('./data/database');
const Note = require('./note/note');
const Account = require('./account/account');
const logger = require('./logging/logger');

/** Models a ranked result from a Query */
class Result {
    /**
     * Constructs a Result
     * 
     * @param {number} score 
     * @param {Note} note 
     */
    constructor(score, note) {
        this.score = score;
        this.note = note;
    }
}

/** Models a note query that can be submitted to a search engine */
class Query {
    /**
     * Constructs a Query
     * 
     * @param {Account} acct The account performing the query
     * @param {string} query The text to search for
     * @param {number} notebookId Optionally restricts the search to a notebook
     */
    constructor(acct, query, notebookId) {
        this.acct = acct;
        this.query = this.prepare(query);
        this.notebookId = notebookId;

        // Raw postgres query that searches within a specific notebook
        // owned by acct.
        this.contextualSearch = `
            SELECT notes.*, ts_rank_cd(title_body_tsv, phrase) as score
            FROM notebook_notes
            CROSS JOIN to_tsquery('pg_catalog.english', ?) phrase
            JOIN notes ON notebook_notes.note_id = notes.id
            JOIN notebooks ON notebook_notes.notebook_id = notebooks.id
            WHERE title_body_tsv @@ phrase
              AND notebooks.owner_id = ?
              AND notes.owner_id = ?
              AND notebooks.id = ?
            ORDER BY score DESC
            LIMIT ?
        `;

        // Raw postgres query that searches all notebooks owned by acct.
        this.contextlessSearch = `
            SELECT notes.*, ts_rank_cd(title_body_tsv, phrase) as score
            FROM notes, to_tsquery('pg_catalog.english', ?) phrase
            WHERE title_body_tsv @@ phrase
              AND owner_id = ?
            ORDER BY score DESC
            LIMIT ?
        `;
    }

    /**
     * Prepares a string to be sent to the search engine
     * @param {string} query Text which may contain characters unfit for
     *                       the search query
     * @returns {string} The prepared query
     */
    prepare(query) {
        return query.replace(/(?!\w|\s)./g, '')
            .replace(/^(\s*)([\W\w]*)(\b\s*$)/g, '$2')
            .replace(/\s+/g, ' | ');
    }

    /**
     * Submits the query to the search engine
     * 
     * This will never return a falsy value, even if an
     * error occurs.
     * @param {number} first Optionally limits the result set to this many items
     * @returns {Promise.<Array.<Result>>} All results that match the query
     */
    async submit(first) {
        first = first ? first : Number.MAX_SAFE_INTEGER;
        
        try {
            let response = await this._submitQuery(first, this.notebookId);
            return response.rows.map(row => {
                const note = this._createNoteFromDbRow(row);
                return new Result(row['score'], note);
            });
        } catch (err) {
            logger.error(err.message, this._createErrorContext());
            return [];
        }
    }

    /**
     * Submits the query to the search engine using the appropriate context
     * @param {number} first Optionally limits the result set to this many items
     * @param {number} notebookId An optional parameter indicating the context where the
     *                            query is run
     * @returns {Object} A Knex database response
     */
    async _submitQuery(first, notebookId) {
        if (notebookId) {
            return await db.raw(
                this.contextualSearch,
                [this.query, this.acct.id, this.acct.id, notebookId, first]
            );
        } else {
            return await db.raw(
                this.contextlessSearch,
                [this.query, this.acct.id, first]
            );
        }
    }

    /**
     * Creates a note using information from a database row
     * @param {Object} row A row from a Knex query result
     * @returns {Note}
     */
    _createNoteFromDbRow(row) {
        return new Note(row['owner_id'], row['title'], row['body'], {
            id: row['id'],
            createdAt: row['created_at'],
            lastModified: row['last_modified'],
            isPublic: row['is_public']
        });
    }

    /**
     * Creates a payload containing diagnostic information about the query
     * @returns {Object}
     */
    _createErrorContext() {
        return {
            accountId: this.acct.id,
            query: this.query,
            notebookSpecified: this.notebookId !== undefined,
            notebookId: this.notebookId
        }
    }
}

module.exports = {
    Result: Result,
    Query: Query
};