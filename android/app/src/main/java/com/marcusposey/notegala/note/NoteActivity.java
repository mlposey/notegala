package com.marcusposey.notegala.note;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.NewNoteInput;

/**
 * Handles the note creation interface and operations
 *
 * This activity can be used to edit and create notes. If editing
 * a note, the intent used to create this activity should
 * be supplied at least one of the following extras:
 *      TITLE_EXTRA
 *      BODY_EXTRA
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

    // Extra key for receiving a title from another fragment or activity
    public static final String TITLE_EXTRA = "TITLE_EXTRA";
    // Extra key for receiving a body from another fragment or activity
    public static final String BODY_EXTRA = "BODY_EXTRA";

    // The context of this activity
    private Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        establishContext();
    }

    /**
     * Performs initial setup based on whether a new note should be created or
     * if an existing one is being edited.
     */
    private void establishContext() {
        String titleExtra = getIntent().getStringExtra(TITLE_EXTRA);
        String bodyExtra = getIntent().getStringExtra(BODY_EXTRA);
        mCtx = (titleExtra == null && bodyExtra == null) ? Context.CREATE : Context.UPDATE;

        if (mCtx == Context.UPDATE) {
            EditText title = findViewById(R.id.edit_note_title);
            title.setText(titleExtra);
            EditText body = findViewById(R.id.edit_note_body);
            body.setText(bodyExtra);
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
        // TODO: NoteActivity.updateNote
    }
}
