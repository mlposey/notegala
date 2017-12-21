'use strict';
process.env.NODE_ENV = 'test';
const { db } = require('../service/database');

/** Removes all rows from each table in the database */
module.exports.clearDB = async () => {
    const tables = ['note_tags', 'note_watchers', 'notes', 'tags', 'users'];
    for (let table of tables) {
        await db(table).del();
    }
};

// The test process will hang if the database connection is not closed.
after(() => {
    db.destroy();
});