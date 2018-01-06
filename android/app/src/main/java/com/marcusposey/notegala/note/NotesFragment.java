package com.marcusposey.notegala.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.marcusposey.notegala.DialogFactory;
import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.ApolloQueryService;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.fragment.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Handles a visual list of notes that can be individually pressed to invoke an edit activity
 */
public abstract class NotesFragment extends ListFragment implements Observer {
    private static final String LOG_TAG = NotesFragment.class.getSimpleName();

    /**
     * Used to sort the list of note cards by their modification timestamp
     *
     * The timestamps look like "Mon Dec 25 2017 20:33:07 GMT+0000 (UTC)" by default, but
     * the timezones are ignored.
     * @see NotesFragment#sortByModified
     */
    private static final SimpleDateFormat mDateFormat =
            new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");

    public NotesFragment() {
        // Required empty public constructor
    }

    /** Use the service to retrieve a new list of notes that are input to listener */
    public abstract void refreshList(QueryService service, QueryService.Listener<List<Note>> listener);
    /** Perform some action when the new note button is pressed */
    public abstract void onNewNotePressed(View view);
    /** Set up the action bar content */
    public abstract void configureAppBar(ActionBar actionBar);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_notes, container, false);

        QueryService.awaitInstance(service -> {
            refreshList(service, this::onNotesNetworkResponse);
            service.addObserver(this);
        });

        configureFloatingActionButtons(root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        QueryService.awaitInstance(service -> service.deleteObserver(this));
    }

    /** Hook sub-buttons to actions */
    private void configureFloatingActionButtons(View rootView) {
        FloatingActionMenu menu = rootView.findViewById(R.id.fab_menu);

        FloatingActionButton newNoteButton = rootView.findViewById(R.id.fab_note);
        newNoteButton.setOnClickListener(view -> {
            menu.close(true);
            onNewNotePressed(view);
        });

        FloatingActionButton newNotebookButton = rootView.findViewById(R.id.fab_notebook);
        newNotebookButton.setOnClickListener(view -> {
            menu.close(true);
            onNewNotebookPress(view);
        });
    }

    /** Attempts to create a new user-defined notebook */
    private void onNewNotebookPress(View view) {
        DialogFactory.input(getContext(), "Notebook Title", title -> {
            QueryService.awaitInstance(service -> {
                service.createNotebook(title, (e, ntbk) -> {
                    getActivity().runOnUiThread(() -> {
                        if (e != null) {
                            Toast.makeText(getContext(), "could not create notebook",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "notebook created",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });
        }).show();
    }

    /**
     * Handles the network response for a notes request
     *
     * Successful requests result in the note list being updated
     * to reflect the current global state of the user's notes.
     */
    private void onNotesNetworkResponse(Exception e, List<Note> notes) {
        getActivity().runOnUiThread(() -> {
            if (e != null) {
                Log.e(LOG_TAG, e.getMessage());
                Toast.makeText(getActivity(), getString(R.string.network_err), Toast.LENGTH_LONG)
                        .show();
            } else {
                Log.i(LOG_TAG, String.format("fetched %d notes", notes.size()));

                Note[] aNotes = notes.toArray(new Note[0]);
                sortByModified(aNotes);
                MyNoteAdapter adapter = new MyNoteAdapter(getActivity(), getFragmentManager(), aNotes);
                getListView().setOnItemClickListener(this::onNoteClicked);
                setListAdapter(adapter);
            }
        });
    }

    /** Handles clicks/presses of the note cards in the list */
    private void onNoteClicked(AdapterView<?> parent, View view, int position, long id) {
        Note note = (Note) parent.getItemAtPosition(position);

        Intent intent = new Intent(getContext(), NoteActivity.class);
        intent.putExtra(NoteActivity.NOTE_EXTRA, new ParcelableNote(note));
        startActivity(intent);
    }

    /**
     * Sorts the collection of notes by modification time
     *
     * Recently updated notes will have a lower index than older ones.
     */
    private void sortByModified(Note[] notes) {
        Arrays.sort(notes, (a, b) -> {
            // Chop off the timezones.
            int aEndPos = a.lastModified().lastIndexOf(" ", a.lastModified().length() - 7);
            int bEndPos = b.lastModified().lastIndexOf(" ", b.lastModified().length() - 7);

            try {
                Date aDate = mDateFormat.parse(a.lastModified().substring(0, aEndPos));
                Date bDate = mDateFormat.parse(b.lastModified().substring(0, bEndPos));
                return -aDate.compareTo(bDate);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "failed to parse lastModified date");
                Log.e(LOG_TAG, e.getMessage());
                return 0;
            }
        });
    }

    /**
     * Updates the note list when network requests are completed
     *
     * This is a hack around Android's startActivityForResult nonsense that doesn't
     * even work and has left me grieving. Grieving, I say!
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof QueryService && arg instanceof ApolloQueryService.ResponseType &&
                arg == ApolloQueryService.ResponseType.NOTE_CHANGE) {
            refreshList((QueryService) o, this::onNotesNetworkResponse);
        }
    }
}
