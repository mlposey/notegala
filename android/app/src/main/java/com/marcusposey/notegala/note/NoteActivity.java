package com.marcusposey.notegala.note;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.EditNoteInput;
import com.marcusposey.notegala.net.gen.NewNoteInput;

/**
 * Handles the note creation interface and operations
 *
 * This activity can be used to edit and create notes. If editing
 * a note, the intent used to create this activity should
 * be supplied these extras:
 *      ID_EXTRA      - String extra  - required
 *      TITLE_EXTRA   - String extra  - optional
 *      BODY_EXTRA    - String extra  - optional
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

    // Extra key for receiving a note's id from another fragment or activity
    public static final String ID_EXTRA = "ID_EXTRA";
    // Extra key for receiving a title from another fragment or activity
    public static final String TITLE_EXTRA = "TITLE_EXTRA";
    // Extra key for receiving a body from another fragment or activity
    public static final String BODY_EXTRA = "BODY_EXTRA";

    // The context of this activity
    private Context mCtx;

    // The original title of the note when the activity was created
    private String mOriginalTitle;
    // The original body of the note when the activity was created
    private String mOriginalBody;

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

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.delete_message))
                .setPositiveButton(android.R.string.yes, (dialog, btn) -> {
                    String noteId = getIntent().getStringExtra(ID_EXTRA);
                    QueryService.awaitInstance(service -> deleteNote(service, noteId));
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    /** Uses the QueryService to trigger note deletion */
    private void deleteNote(QueryService service, String noteId) {
        service.removeNote(noteId, (e, didSucceed) -> {
            runOnUiThread(() -> {
                if (e != null || didSucceed == null || !didSucceed) {
                    Toast.makeText(getApplicationContext(), "could not delete note",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "note deleted",
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
        String idExtra = getIntent().getStringExtra(ID_EXTRA);
        mCtx = (idExtra == null) ? Context.CREATE : Context.UPDATE;

        if (mCtx == Context.UPDATE) {
            EditText title = findViewById(R.id.edit_note_title);
            title.setText(getIntent().getStringExtra(TITLE_EXTRA));
            mOriginalTitle = title.getText().toString();

            EditText body = findViewById(R.id.edit_note_body);
            body.setText(getIntent().getStringExtra(BODY_EXTRA));
            mOriginalBody = body.getText().toString();
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
        // TODO: Serialize note tags.

        return NewNoteInput.builder().title(title).body(body).build();
    }

    /** Publishes a new note to the API */
    private void createNote(NewNoteInput note, QueryService service) {
        if (note.body().isEmpty()) {
            Log.i(LOG_TAG, "skipped upload of note with empty body");
            return;
        }

        service.createNote(note, (e, response) -> {
            runOnUiThread(() -> {
                if (e != null) {
                    Log.e(LOG_TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), "network error",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.i(LOG_TAG, "note created");
                    Toast.makeText(getApplicationContext(), "note created",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        });
    }

    /** Publishes the updated version of a note to the API */
    private void updateNote(NewNoteInput note, QueryService service) {
        if (note.body().equals(mOriginalBody) && note.title().equals(mOriginalTitle)) {
            Log.i(LOG_TAG, "skipped update of unchanged note");
            return;
        }

        EditNoteInput.Builder builder = EditNoteInput.builder()
                .id(getIntent().getStringExtra(ID_EXTRA))
                .title(note.title())
                .body(note.body());

        service.editNote(builder.build(), (e, response) -> {
            runOnUiThread(() -> {
                if (e != null) {
                    Log.e(LOG_TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), "network error",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.i(LOG_TAG, "note updated");
                    Toast.makeText(getApplicationContext(), "note updated",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        });
    }
}
