package com.marcusposey.notegala.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.MyNotesQuery;

import java.util.List;

/** Handles notes that the user owns */
public class MyNotesFragment extends ListFragment {
    private static final String LOG_TAG = MyNotesFragment.class.getSimpleName();

    // Initial app startup loads this fragment twice. We don't
    // want to perform the network call the second time, i.e.,
    // when this equals 1;
    private static int sLoadCount = 0;

    private MyNoteAdapter myNoteAdapter;

    public MyNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_notes, container, false);

        configureFloatingActionButtons(root);
        return root;
    }

    private void configureFloatingActionButtons(View rootView) {
        FloatingActionMenu menu = rootView.findViewById(R.id.fab_menu);

        FloatingActionButton newNoteButton = rootView.findViewById(R.id.fab_note);
        newNoteButton.setOnClickListener(view -> {
            menu.close(true);
            Intent intent = new Intent(getContext(), NoteActivity.class);
            startActivity(intent);
        });

        FloatingActionButton newNotebookButton = rootView.findViewById(R.id.fab_notebook);
        newNotebookButton.setOnClickListener(view -> {
            Toast.makeText(getContext(), "not yet implemented", Toast.LENGTH_SHORT).show();
            menu.close(true);
        });
    }

    /** Handles the network response for a notes request */
    private void onNotesNetworkResponse(Exception e, List<MyNotesQuery.Note> notes) {
        getActivity().runOnUiThread(() -> {
            Log.i(LOG_TAG, "got notes network response");

            if (e != null) {
                Log.e(LOG_TAG, e.getMessage());
                Toast.makeText(getActivity(), "network error", Toast.LENGTH_LONG).show();
            } else {
                Log.i(LOG_TAG, String.format("fetched %d notes", notes.size()));

                MyNotesQuery.Note[] aNotes = notes.toArray(new MyNotesQuery.Note[0]);
                if (myNoteAdapter == null) {
                    myNoteAdapter = new MyNoteAdapter(getActivity(), getFragmentManager(), aNotes);
                    setListAdapter(myNoteAdapter);
                } else {
                    myNoteAdapter.refresh(aNotes);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sLoadCount++ != 1) {
            QueryService.awaitInstance(service -> service.getMyNotes(this::onNotesNetworkResponse));
        }
    }
}
