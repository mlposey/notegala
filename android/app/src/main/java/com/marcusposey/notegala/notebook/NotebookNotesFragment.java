package com.marcusposey.notegala.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
    public static final String NOTEBOOK_ID = "NOTEBOOK_ID";

    // Bundle/argument key for the notebook title
    public static final String NOTEBOOK_TITLE = "NOTEBOOK_TITLE";

    // The unique id of the notebook this fragment is focused on
    private String mNotebookId;

    @Override
    public void refreshList(QueryService service, QueryService.Listener<List<Note>> listener) {
        service.getNotebookNotes(mNotebookId, listener);
    }

    @Override
    public void onNewNotePressed(View view) {
        Intent intent = new Intent(getContext(), NoteActivity.class);
        intent.putExtra(NoteActivity.NOTEBOOK_ID_EXTRA, mNotebookId);
        startActivity(intent);
    }

    /**
     * Sets the title of the action bar to the notebook title
     *
     * There must exist a bundle string under the key NotebookNotesFragment.NOTEBOOK_TITLE
     * before calling this method.
     */
    @Override
    public void configureAppBar(ActionBar actionBar) {
        Bundle bundle = getArguments();
        actionBar.setTitle(bundle.getString(NOTEBOOK_TITLE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mNotebookId = bundle.getString(NOTEBOOK_ID);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
