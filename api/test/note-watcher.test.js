'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../service/database');
const { clearDB } = require('./index');
const NoteFactory = require('../model/note/note-factory');
const Account = require('../model/account');
const NoteWatcher = require('../model/note/note-watcher');

const users = Object.freeze([
    {email: 'test1@example.com', name: 'Test One'},
    {email: 'test2@example.com', name: 'Test Two'}
]);
const newNoteInput = Object.freeze({
    title: 'Test',
    body: 'Test'
});

describe('NoteWatcher', () => {
    describe('#changeEditPerm()', () => {
        beforeEach(async () => await clearDB());

        it('should set edit permissions to boolean argument', async () => {
            await Account.construct(users[0].email, users[0].name);
            await Account.construct(users[1].email, users[1].name);

            const note =
                await NoteFactory.construct(users[0].email, newNoteInput);
            await note.addWatcher(users[1].email, false);

            let watchers = await note.watchers();
            const toChange = watchers
                .find(watcher => watcher.name === users[1].name);

            await toChange.changeEditPerm(true);
            watchers = await note.watchers();

            const expectedChange = watchers
                .find(watcher => watcher.name === users[1].name);
            expectedChange.canEdit.should.eql(true);
        });
    });

    describe('#earliest(watchers)', () => {
        beforeEach(async () => await clearDB());
        
        it('should return the earliest watcher', async () => {
            await Account.construct(users[0].email, users[0].name);
            await Account.construct(users[1].email, users[1].name);

            const note =
                await NoteFactory.construct(users[0].email, newNoteInput);
            await note.addWatcher(users[1].email, false);

            const earliest = await NoteWatcher
                .earliest(await note.watchers());
            earliest.name.should.eql(users[0].name);
        });

        it('should return null if the arg contains no watchers', () => {
            Promise.all([
                NoteWatcher.earliest(undefined),
                NoteWatcher.earliest(null),
                NoteWatcher.earliest([])
            ])
            .then(values => {
                values.filter(val => val != null).length.should.eql(0);
            });
        });
    });
});