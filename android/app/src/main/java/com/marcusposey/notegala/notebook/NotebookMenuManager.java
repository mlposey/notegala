package com.marcusposey.notegala.notebook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.marcusposey.notegala.R;
import com.marcusposey.notegala.SidePane;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.net.gen.query.MyNotebooksHeadQuery;
import com.marcusposey.notegala.note.NotesFragment;

import java.util.HashMap;

import javax.annotation.Nullable;

/** Manages a collection of notebooks as menu items */
public class NotebookMenuManager {
    private static final String LOG_TAG = NotebookMenuManager.class.getSimpleName();

    // The activity that created the menu that will hold the notebook items
    private AppCompatActivity mParent;

    // The menu holding the notebook items
    private SubMenu mMenu;

    // The number of items in the menu that appear before this collection
    private int mOffset;

    // Header information about each notebook
    private HashMap<String, MyNotebooksHeadQuery.Notebook> mHeaders = new HashMap<>();

    /**
     * Constructs the manager
     * @param parent The activity that created the menu that will hold the notebook items
     * @param menu The menu to place the items in; will be cleared
     * @param offset The number of items in the menu that appear before this collection
     */
    public NotebookMenuManager(AppCompatActivity parent, SubMenu menu, int offset) {
        mMenu = menu;
        mOffset = offset;
        mParent = parent;

        refresh(null, null);
    }

    /** Loads notes into the content frame that belong to the notebook indicated by item */
    public void onItemSelected(MenuItem item) {
        MyNotebooksHeadQuery.Notebook nbHeader = mHeaders.get(item.getTitle());

        NotesFragment fragment = new NotebookNotesFragment();
        Bundle args = new Bundle();
        args.putString(NotebookNotesFragment.NOTEBOOK_ID, nbHeader.id());
        args.putString(NotebookNotesFragment.NOTEBOOK_TITLE, nbHeader.title());
        fragment.setArguments(args);

        fragment.configureAppBar(mParent.getSupportActionBar());
        mParent.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    /**
     * Repopulates the notebook list with the most recent network state
     *
     * @param title If given a notebook title, that content will fill the
     *              content frame after the refresh.
     * @param parent If given a notebook title, parent is used to check the
     *               new tab item which corresponds to the content frame.
     */
    public void refresh(@Nullable String title, @Nullable SidePane parent) {
        Log.i(LOG_TAG, "refreshing notebook headers");
        mMenu.clear();
        mHeaders.clear();

        QueryService.awaitInstance(service -> {
            service.getNotebookHeaders(((e, headers) -> {
                mParent.runOnUiThread(() -> {
                    if (e != null) {
                        Log.e(LOG_TAG, e.getMessage());
                        return;
                    }

                    MenuItem newHome = null;
                    int pos = mOffset;

                    for (MyNotebooksHeadQuery.Notebook notebook : headers) {
                        Log.i(LOG_TAG, "header - " + notebook.title());
                        mHeaders.put(notebook.title(), notebook);

                        MenuItem item = mMenu.add(0, pos++, Menu.NONE, notebook.title());
                        item.setIcon(R.drawable.ic_book_black_24dp);

                        if (title != null && notebook.title().equals(title)) newHome = item;
                    }

                    if (title != null) parent.onNavigationItemSelected(newHome);
                });
            }));
        });
    }
}
