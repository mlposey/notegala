package com.marcusposey.notegala.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marcusposey.notegala.DialogFactory;
import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.Note;
import com.marcusposey.notegala.note.NoteActivity;
import com.marcusposey.notegala.note.NotesFragment;

import java.util.List;

/** Manages a list of notes that belong to a particular notebook */
public class NotebookNotesFragment extends NotesFragment {
    private static final String LOG_TAG = NotebookNotesFragment.class.getSimpleName();

    // Bundle/argument key for the note id
    public static final String NOTEBOOK_ID = "NOTEBOOK_ID";

    // Bundle/argument key for the notebook title
    public static final String NOTEBOOK_TITLE = "NOTEBOOK_TITLE";

    // The unique id of the notebook this fragment is focused on
    private String mNotebookId;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notebook, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.notebook_menu_delete) {
            DialogFactory.deletion(getContext(), getString(R.string.dialog_notebook),
                    () -> QueryService.awaitInstance(this::deleteNotebook)
            ).show();
        }
        return true;
    }

    private void deleteNotebook(QueryService service) {
        service.removeNotebook(mNotebookId, (e, wasRemoved) -> {
            getActivity().runOnUiThread(() -> {
                if (e != null || wasRemoved == null || !wasRemoved.booleanValue()) {
                    Toast.makeText(getContext(), getString(R.string.fragment_notebook_failed_delete),
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, e != null ? e.getMessage() : "could not delete notebook");
                } else {
                    Toast.makeText(getContext(), getString(R.string.fragment_notebook_delete),
                            Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, "deleted notebook " + mNotebookId);
                }
            });
        });
    }

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
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        mNotebookId = bundle.getString(NOTEBOOK_ID);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
