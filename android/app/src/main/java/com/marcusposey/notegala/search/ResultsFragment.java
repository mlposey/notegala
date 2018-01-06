package com.marcusposey.notegala.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.gen.fragment.Note;
import com.marcusposey.notegala.net.gen.query.SearchQuery;
import com.marcusposey.notegala.note.NoteActivity;
import com.marcusposey.notegala.note.NoteAdapter;
import com.marcusposey.notegala.note.ParcelableNote;

import java.util.Arrays;
import java.util.List;

/** Handles a visual list results from a search */
public class ResultsFragment extends ListFragment {
    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    /** Populates the list view with all notes from the results */
    public void showResults(List<SearchQuery.Match> results) {
        SearchQuery.Match[] matches = results.toArray(new SearchQuery.Match[0]);
        // Show more relevant items first.
        Arrays.sort(matches, (a, b) -> a.score() > b.score() ? -1 : 1);

        Note[] notes = new Note[matches.length];
        for (int i = 0; i < notes.length; i++) notes[i] = matches[i].note();

        NoteAdapter adapter = new NoteAdapter(getActivity(), getFragmentManager(), notes);
        getListView().setOnItemClickListener(this::onNoteClicked);
        setListAdapter(adapter);
    }

    /** Handles clicks/presses of the note cards in the list */
    private void onNoteClicked(AdapterView<?> parent, View view, int position, long id) {
        Note note = (Note) parent.getItemAtPosition(position);

        Intent intent = new Intent(getContext(), NoteActivity.class);
        intent.putExtra(NoteActivity.NOTE_EXTRA, new ParcelableNote(note));
        startActivity(intent);

        // TODO: If they edit a note here, the result list won't show the updated version.
    }
}
