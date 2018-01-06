package com.marcusposey.notegala.note;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.fragment.Note;

import java.util.List;

/** Manages a complete list of the user's personal notes */
public class MyNotesFragment extends NotesFragment {
    @Override
    public void refreshList(QueryService service, QueryService.Listener<List<Note>> listener) {
        service.getMyNotes(listener);
    }

    @Override
    public void onNewNotePressed(View view) {
        Intent intent = new Intent(getContext(), NoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void configureAppBar(ActionBar actionBar) {
        actionBar.setTitle("My Notes");
    }
}
