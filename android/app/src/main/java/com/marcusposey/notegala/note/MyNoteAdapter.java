package com.marcusposey.notegala.note;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.content_my_note, parent, false);

        MyNotesQuery.Note note = mNotes[pos];
        TextView body = row.findViewById(R.id.my_note_body);
        body.setText(note.body());

        TextView title = row.findViewById(R.id.my_note_title);
        if (note.title() == null || note.title().isEmpty()) {
            ((ViewGroup) title.getParent()).removeView(title);
            expandTopBottomMargins(body, 30);
        } else {
            title.setText(note.title());
        }

        return row;
    }

    /**
     * Expands the top and bottom margins of the view
     * @param amount The margin space (in dp) to add to the current state
     */
    private void expandTopBottomMargins(View view, int amount) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();

        params.setMargins(
                params.leftMargin,
                params.topMargin + amount,
                params.rightMargin,
                params.bottomMargin + amount
        );
        view.setLayoutParams(params);
    }
}
