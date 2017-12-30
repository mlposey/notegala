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

        # Retrieves all notebooks owned by the requester
        #
        # first - The maximum number of notebooks to retrieve        
        myNotebooks(first: Int): [Notebook!]!
    }

    type Mutation {
        # Creates a new note and returns its
        # full definition.
        createNote(input: NewNoteInput!): Note!

        # Modifies the contents of an existing note
        # and returns its new representation
        editNote(input: EditNoteInput!): Note!

        # Removes the note from the user's collection
        #
        # If the requester is also the owner of this
        # note, the oldest watcher will become the new
        # owner.
        #
        # Returns true if the note was removed; false
        # otherwise
        removeNote(id: ID!): Boolean!

        # Creates a new note with the specified name
        #
        # A user cannot have multiple notebooks with
        # the same name.
        createNotebook(title: String!): Notebook!
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
    #
    # This object must have a value for at least one of
    # title or body. I.e., they cannot both be blank.
    #
    # See createNote for an example use case.
    input NewNoteInput {
        # The title of the note
        title: String

        # The main content of the note
        body: String

        # Tags used to categorize the note
        tags: [String!]

        # The notebook where the note should be attached
        notebook: ID
    }

    # Used to change the content of an existing note
    input EditNoteInput {
        # The id of the existing note to change
        id: ID!

        # The new note title
        # If null, the old value is kept.
        # If ' ', the title is erased.
        title: String

        # The new note body
        # If null, the old value is kept
        # If ' ', the title is erased.
        body: String

        # The new note tag list
        # If null, the old value is kept.
        # If empty, the old tags are removed.
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

    # Describes a named collection of notes
    type Notebook {
        # The unique id of the notebook
        id: ID!

        # Indicates the timestamp with timezone when the
        # notebook was created
        createdAt: String!

        # The id of the notebook owner
        owner: ID!

        # The name of the notebook
        title: String!

        # All notes contained in the notebook
        notes: [Note!]!
    }
`);