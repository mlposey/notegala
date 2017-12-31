package com.marcusposey.notegala.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.Note;
import com.marcusposey.notegala.note.NoteActivity;
import com.marcusposey.notegala.note.NotesFragment;

import java.util.List;

/** Manages a list of notes that belong to a particular notebook */
public class NotebookNotesFragment extends NotesFragment {
    // Bundle/argument key for the note id
    public static final String NOTE_ID = "NOTE_ID";

    // The unique id of the note this fragment is focused on
    private String mNoteId;

    @Override
    public void refreshList(QueryService service, QueryService.Listener<List<Note>> listener) {
        service.getNotebookNotes(mNoteId, listener);
    }

    @Override
    public void onNewNotePressed(View view) {
        Intent intent = new Intent(getContext(), NoteActivity.class);
        intent.putExtra(NoteActivity.NOTEBOOK_ID_EXTRA, mNoteId);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mNoteId = bundle.getString(NOTE_ID);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
