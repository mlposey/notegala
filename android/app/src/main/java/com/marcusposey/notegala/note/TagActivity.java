package com.marcusposey.notegala.note;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.marcusposey.notegala.R;

import java.util.ArrayList;

/** Manages the display and modification of a note's tags */
public class TagActivity extends ListActivity {

    /** Places tag content into a layout */
    private class Adapter extends ArrayAdapter<String> {
        private View.OnClickListener mOnRemoveTagClicked;

        public Adapter(Context context, FragmentManager manager, ArrayList<String> tags) {
            super(context, -1, tags);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.content_tag, parent, false);

            TextView tag = row.findViewById(R.id.tag_name);
            tag.setText(getItem(pos));

            ImageButton bin = row.findViewById(R.id.button_del_tag);
            bin.setOnClickListener(mOnRemoveTagClicked);

            return row;
        }

        /** Sets the listener that is triggered when the delete icon of a view group is pressed */
        public void setOnRemoveTagListener(View.OnClickListener listener) {
            mOnRemoveTagClicked = listener;
        }
    }

    private Adapter mAdapter;
    public static ArrayList<String> sTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        EditText input = findViewById(R.id.input_tag);
        input.setOnKeyListener(this::onTagInputKeyPress);

        mAdapter = new Adapter(getApplicationContext(), getFragmentManager(), sTags);
        mAdapter.setOnRemoveTagListener(this::onRemoveTag);
        setListAdapter(mAdapter);
    }

    /**
     * Removes a tag from the list view
     *
     * This action is triggered by a pressing a delete icon that is
     * located next to each tag in the list.
     */
    public void onRemoveTag(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        String tag = ((TextView) parent.findViewById(R.id.tag_name)).getText().toString();

        mAdapter.remove(tag);
        mAdapter.notifyDataSetChanged();
    }

    /** Waits for new tag inputs and adds them to the adapter view */
    public boolean onTagInputKeyPress(View v, int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_ENTER) return false;

        EditText input = (EditText) v;
        String tag = input.getText().toString().trim();
        if (tag.isEmpty()) return false;

        mAdapter.add(tag);
        mAdapter.notifyDataSetChanged();

        input.getText().clear();
        return true;
    }

    /** Returns the current set of tags */
    public static ArrayList<String> getTags() {
        return sTags;
    }
}
