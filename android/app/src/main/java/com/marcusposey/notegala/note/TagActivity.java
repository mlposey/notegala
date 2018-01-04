package com.marcusposey.notegala.note;

import android.app.ListActivity;
import android.os.Bundle;

import com.marcusposey.notegala.R;

/** Manages the display and modification of a note's tags */
public class TagActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
    }
}
