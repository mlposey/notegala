package com.marcusposey.notegala.note;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.gen.MyNotesQuery;

/** Maps owned notes to cards that display summaries of content */
public class MyNoteAdapter extends ArrayAdapter<MyNotesQuery.Note> {
    private final Context mContext;
    private MyNotesQuery.Note[] mNotes;

    public MyNoteAdapter(Context context, FragmentManager manager, MyNotesQuery.Note[] notes) {
        super(context, -1, notes);
        mContext = context;
        mNotes = notes;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // Show new notes first.
        pos = mNotes.length - 1 - pos;

        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.content_my_note, parent, false);

        ((TextView) row.findViewById(R.id.my_note_title)).setText(mNotes[pos].title());
        ((TextView) row.findViewById(R.id.my_note_body)).setText(mNotes[pos].body());

        return row;
    }

    /** Repopulates the adapter with a new set of notes */
    public void refresh(MyNotesQuery.Note[] notes) {
        mNotes = notes;
        notifyDataSetChanged();
    }
}
