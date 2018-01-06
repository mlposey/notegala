'use strict';
const { db } = require('../service/database');
const Note = require('./note/note');
const Account = require('./account');

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
     * @param {number} first Optionally limits the result set to this many items
     */
    constructor(acct, query, first) {
        this.acct = acct;
        this.query = this.prepare(query);
        this.first = first ? first : Number.MAX_SAFE_INTEGER;
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
     * @returns {Promise.<Array.<Result>>} All results that match the query
     */
    async submit() {
        try {
            let response = await db.raw(`
                SELECT notes.*, ts_rank_cd(title_body_tsv, phrase) as score
                FROM notes, to_tsquery(?) phrase
                WHERE title_body_tsv @@ phrase
                  AND owner_id = ?
                ORDER BY score DESC
                LIMIT ?
            `, [this.query, this.acct.id, this.first]);

            return response.rows.map(row => {
                const note = new Note(row['id'], row['owner_id'],
                    row['created_at'], row['last_modified'],
                    row['is_public'], row['title'], row['body']);
                return new Result(row['score'], note);
            });
        } catch (err) {
            console.log(err.message);
            return [];
        }
    }
}

module.exports = {
    Result: Result,
    Query: Query
};