package com.marcusposey.notegala.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.MyNotesQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Handles a visual list of all notes that the user owns
 *
 * The owner of a note is typically the person who created it. However, ownership
 * may be transferable in the future. When you think of a note as being "yours", this
 * essentially means that, in addition to edit privileges, you can change the access
 * privileges of other watchers.
 */
public class MyNotesFragment extends ListFragment implements Observer {
    private static final String LOG_TAG = MyNotesFragment.class.getSimpleName();

    /**
     * Used to sort the list of note cards by their modification timestamp
     *
     * The timestamps look like "Mon Dec 25 2017 20:33:07 GMT+0000 (UTC)" by default, but
     * the timezones are ignored.
     * @see MyNotesFragment#sortByModified
     */
    private static final SimpleDateFormat mDateFormat =
            new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");

    public MyNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_notes, container, false);

        QueryService.awaitInstance(service -> {
            service.getMyNotes(this::onNotesNetworkResponse);
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

    /**
     * Handles the network response for a notes request
     *
     * Successful requests result in the note list being updated
     * to reflect the current global state of the user's notes.
     */
    private void onNotesNetworkResponse(Exception e, List<MyNotesQuery.Note> notes) {
        getActivity().runOnUiThread(() -> {
            if (e != null) {
                Log.e(LOG_TAG, e.getMessage());
                Toast.makeText(getActivity(), "network error", Toast.LENGTH_LONG).show();
            } else {
                Log.i(LOG_TAG, String.format("fetched %d notes", notes.size()));

                MyNotesQuery.Note[] aNotes = notes.toArray(new MyNotesQuery.Note[0]);
                sortByModified(aNotes);
                MyNoteAdapter adapter = new MyNoteAdapter(getActivity(), getFragmentManager(), aNotes);
                getListView().setOnItemClickListener(this::onNoteClicked);
                setListAdapter(adapter);
            }
        });
    }

    /** Handles clicks/presses of the note cards in the list */
    private void onNoteClicked(AdapterView<?> parent, View view, int position, long id) {
        MyNotesQuery.Note note = (MyNotesQuery.Note) parent.getItemAtPosition(position);

        Intent intent = new Intent(getContext(), NoteActivity.class);
        intent.putExtra(NoteActivity.TITLE_EXTRA, note.title());
        intent.putExtra(NoteActivity.BODY_EXTRA, note.body());
        intent.putExtra(NoteActivity.ID_EXTRA, note.id());
        startActivity(intent);
    }

    /**
     * Sorts the collection of notes by modification time
     *
     * Recently updated notes will have a lower index than older ones.
     */
    private void sortByModified(MyNotesQuery.Note[] notes) {
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
        if (o instanceof QueryService) {
            ((QueryService) o).getMyNotes(this::onNotesNetworkResponse);
        }
    }
}
