'use strict';
process.env.NODE_ENV = 'test';

const chai = require('chai');
const should = chai.should();

const { db } = require('../app/service/database');
const { clearDB } = require('./index');
const NoteFactory = require('../app/note/note-factory');
const Account = require('../app/account/account');
const AccountRepository = require('../app/account/account-repository');
const NoteWatcher = require('../app/note/note-watcher');

const users = Object.freeze([
    {email: 'test1@example.com', name: 'Test One'},
    {email: 'test2@example.com', name: 'Test Two'}
]);
const newNoteInput = Object.freeze({
    title: 'Test',
    body: 'Test'
});

const accountRepo = new AccountRepository();

describe('NoteWatcher', () => {
    describe('#changeEditPerm()', () => {
        beforeEach(async () => await clearDB());

        it('should set edit permissions to boolean argument', async () => {
            const userA = new Account(users[0].email, users[0].name);
            await accountRepo.add(userA);
            const userB = new Account(users[1].email, users[1].name);
            await accountRepo.add(userB);

            const notepad = await NoteFactory.construct(userA, newNoteInput);
            let note = notepad.note;
            note.isPublic = true;
            await note.addWatcher(userB.id, false);

            let watchers = await note.watchers();
            const toChange = watchers
                .find(watcher => watcher.name === userB.name);

            await toChange.changeEditPerm(true);
            watchers = await note.watchers();

            const expectedChange = watchers
                .find(watcher => watcher.name === userB.name);
            expectedChange.canEdit.should.eql(true);
        });
    });

    describe('#earliest(watchers)', () => {
        beforeEach(async () => await clearDB());
        
        it('should return the earliest watcher', async () => {
            const userA = new Account(users[0].email, users[0].name);
            await accountRepo.add(userA);
            const userB = new Account(users[1].email, users[1].name);
            await accountRepo.add(userB);
            
            const notepad = await NoteFactory.construct(userA, newNoteInput);
            const note = notepad.note;
            await note.addWatcher(userB.id, false);

            const earliest = await NoteWatcher
                .earliest(await note.watchers());
            earliest.name.should.eql(userA.name);
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