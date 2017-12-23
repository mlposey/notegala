package com.marcusposey.notegala;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.NewNoteInput;

/** Handles the note creation interface and operations */
public class NoteActivity extends AppCompatActivity {
    private static final String LOG_TAG = NoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
    }

    /** Uploads the content of the note to the server */
    @Override
    public void onBackPressed() {
        NewNoteInput note = serializeContent();
        if (note == null) {
            Log.i(LOG_TAG, "skipped upload of note with empty body");
            super.onBackPressed();
            return;
        }

        QueryService.awaitInstance(service -> {
            service.createNote(note, (e, response) -> {
                if (e != null) {
                    Log.e(LOG_TAG, e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "network error",
                                Toast.LENGTH_LONG).show();
                    });
                } else {
                    finish();
                }
            });
        });

        super.onBackPressed();
    }

    /** Turns the note content into a format that can be sent to the server */
    private NewNoteInput serializeContent() {
        String title = ((EditText) findViewById(R.id.edit_note_title)).getText().toString();
        String body = ((EditText) findViewById(R.id.edit_note_body)).getText().toString();
        if (body.isEmpty()) return null;
        // TODO: Serialize note tags.

        return NewNoteInput.builder().title(title).body(body).build();
    }
}
