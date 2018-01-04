package com.marcusposey.notegala.note;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.marcusposey.notegala.DialogFactory;
import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.EditNoteInput;
import com.marcusposey.notegala.net.gen.NewNoteInput;
import com.marcusposey.notegala.net.gen.Note;

/**
 * Handles the note creation interface and operations
 *
 * This activity can be used to edit and create notes. If editing
 * a note, the intent used to create this activity should
 * be given a ParcelableNote extra using the NoteActivity.NOTE_EXTRA key.
 */
public class NoteActivity extends AppCompatActivity {

    /** Describes the reason the activity was started */
    private enum Context {
        // The activity should create a new note
        CREATE,
        // The activity should edit an existing note
        UPDATE
    }

    private static final String LOG_TAG = NoteActivity.class.getSimpleName();

    // Extra key for receiving the id of the notebook where the note will go
    public static final String NOTEBOOK_ID_EXTRA = "NOTEBOOK_ID_EXTRA";

    // Extra key for receiving a ParcelableNote
    public static final String NOTE_EXTRA = "NOTE_EXTRA";

    // Holds the note that was initially loaded into the activity if
    // the context is Context.UPDATE
    private Note mNote;

    // The context of this activity
    private Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setSupportActionBar(findViewById(R.id.note_toolbar));

        establishContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.note_menu_delete) {
            handleDeletePressed();
        }
        return true;
    }

    /**
     * Creates a dialog to confirm that the note should be deleted
     *
     * Confirmation results in deletion and activity finish if the activity
     * context is Context.UPDATE. If it is Context.CREATE, the activity
     * is simply finished.
     */
    private void handleDeletePressed() {
        if (mCtx == Context.CREATE) {
            finish();
            return;
        }

        DialogFactory.deletion(this, getString(R.string.dialog_note), () -> {
            QueryService.awaitInstance(service -> deleteNote(service, mNote.id()));
        }).show();
    }

    /** Uses the QueryService to trigger note deletion */
    private void deleteNote(QueryService service, String noteId) {
        service.removeNote(noteId, (e, didSucceed) -> {
            runOnUiThread(() -> {
                if (e != null || didSucceed == null || !didSucceed) {
                    Toast.makeText(getApplicationContext(), "could not delete note",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.activity_note_ndelete),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    /**
     * Performs initial setup based on whether a new note should be created or
     * if an existing one is being edited.
     */
    private void establishContext() {
        ParcelableNote encoded = getIntent().getParcelableExtra(NOTE_EXTRA);
        mCtx = (encoded == null) ? Context.CREATE : Context.UPDATE;

        if (mCtx == Context.UPDATE) {
            mNote = encoded.getNote();

            EditText title = findViewById(R.id.edit_note_title);
            title.setText(mNote.title());
            EditText body = findViewById(R.id.edit_note_body);
            body.setText(mNote.body());
        }
    }

    /** Uploads the content of the note to the server */
    @Override
    public void onBackPressed() {
        NewNoteInput note = serializeContent();
        QueryService.awaitInstance(service -> {
            runOnUiThread(() -> {
                if (mCtx == Context.CREATE) {
                    createNote(note, service);
                } else {
                    updateNote(note, service);
                }
            });
        });

        super.onBackPressed();
    }

    /** Turns note content into a format that can be sent to the server */
    private NewNoteInput serializeContent() {
        String title = ((EditText) findViewById(R.id.edit_note_title)).getText().toString();
        String body = ((EditText) findViewById(R.id.edit_note_body)).getText().toString();
        String notebookId = getIntent().getStringExtra(NOTEBOOK_ID_EXTRA);
        // TODO: Serialize note tags.

        return NewNoteInput.builder().title(title).body(body).notebook(notebookId).build();
    }

    /** Publishes a new note to the API */
    private void createNote(NewNoteInput note, QueryService service) {
        if ((note.body() == null || note.body().isEmpty()) &&
            (note.title() == null || note.title().isEmpty())) {
            Log.i(LOG_TAG, "skipped upload of note with empty body");
            return;
        }

        service.createNote(note, (e, response) -> {
            runOnUiThread(() -> {
                if (e != null) {
                    Log.e(LOG_TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), getString(R.string.network_err),
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.i(LOG_TAG, "note created");
                    Toast.makeText(getApplicationContext(), getString(R.string.activity_note_ncreate),
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        });
    }

    /** Publishes the updated version of a note to the API */
    private void updateNote(NewNoteInput note, QueryService service) {
        if (note.body().equals(mNote.body()) && note.title().equals(mNote.title())) {
            Log.i(LOG_TAG, "skipped update of unchanged note");
            return;
        }

        // The single spaces are like clear commands. This is a hack around
        // the express graphql implementation.
        EditNoteInput.Builder builder = EditNoteInput.builder()
                .id(mNote.id())
                .title((note.title() == null || note.title().isEmpty()) && mNote.title() != null ? " " : note.title())
                .body((note.body() == null || note.body().isEmpty()) && !mNote.body().isEmpty() ? " " : note.body());

        service.editNote(builder.build(), (e, response) -> {
            runOnUiThread(() -> {
                if (e != null) {
                    Log.e(LOG_TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), getString(R.string.network_err),
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.i(LOG_TAG, "note updated");
                    Toast.makeText(getApplicationContext(), getString(R.string.activity_note_nupdate),
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        });
    }
}
