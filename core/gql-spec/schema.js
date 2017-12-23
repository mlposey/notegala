'use strict';
const { buildSchema } = require('graphql');

// Schema for the GraphQL API
module.exports.schema = buildSchema(`
    type Query {
        # Retrieves the requester's account
        #
        # This query relies on the supplied bearer token
        # to uniquely identify an account. An account
        # will be created if one does not already exist.
        account: Account!

        # Retrieves all notes owned by the requester
        #
        # first - The maximum number of notes to retrieve
        myNotes(first: Int): [Note!]!
    }

    type Mutation {
        # Creates a new note, returning the full
        # definition.
        createNote(input: NewNoteInput!): Note!
    }

    # Describes a user account
    type Account {
        # Unique id for the account
        id: ID!

        # Timestamp with timezone indicating sign up time
        createdAt: String!

        # Timestamp with timezone indicating last sign in
        lastSeen: String!

        # A unique email address
        email: String!

        # The display name others see when interacting with
        # this account -- not guaranteed to be unique
        name: String!
    }

    # Used in the creation of new notes
    # See createNote for an example use case.
    input NewNoteInput {
        # The title of the note
        # This is optional and not required to be unique.
        title: String

        # The main content of the note
        body: String!

        # Tags used to categorize the note
        # Notes can be tagless.
        tags: [String!]
    }

    # Details the full state of a note
    type Note {
        # A unique id for the note
        id: ID!

        # The id of the user that owns the note
        # The note owner can manage the edit permissions
        # of all watchers.
        ownerId: ID!

        # Timestamp with timezone indicating creation time
        createdAt: String!

        # Timestamp with timezone indicating the last time
        # the note content (title or body) was changed
        lastModified: String!

        # Indicates privacy level
        # Public notes can be discovered by all users, while
        # private ones belong to the single watcher.
        isPublic: Boolean!

        # The title of the note
        # Notes do not require a title.
        title: String

        # The main content of the note
        body: String!

        # Tags used to categorize the note
        # Notes do not require tags.
        tags: [String!]!

        # Users that hold this same note
        # All notes have a minimum of one watcher.
        watchers: [NoteWatcher!]!
    }

    # Describes a user that subscribes to a note's content
    type NoteWatcher {
        # The unique id of the user
        id: ID!

        # The display name of the user
        name: String!

        # Indicates if the user can make changes to the note
        canEdit: Boolean!
    }
`);