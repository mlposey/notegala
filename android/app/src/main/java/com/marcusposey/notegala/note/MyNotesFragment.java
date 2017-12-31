package com.marcusposey.notegala.note;

import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.Note;

import java.util.List;

/** Manages a complete list of the user's personal notes */
public class MyNotesFragment extends NotesFragment {
    @Override
    public void refreshList(QueryService service, QueryService.Listener<List<Note>> listener) {
        service.getMyNotes(listener);
    }
}
