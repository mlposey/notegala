package com.marcusposey.notegala;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.TextView;

import com.marcusposey.notegala.net.gen.GetAccountQuery;
import com.marcusposey.notegala.note.MyNotesFragment;
import com.marcusposey.notegala.note.NotesFragment;
import com.marcusposey.notegala.notebook.NotebookMenuManager;

/**
 * Processes events related to the left navigation drawer
 *
 * The drawer acts as an entry-point to major application components
 * such as personal notes, notebooks, or the explore feature.
 */
public class SidePane extends NavigationView {
    private MainActivity mParent;
    private DrawerLayout mDrawer;
    private NotebookMenuManager mNotebookMenu;

    public SidePane(Context context) {
        super(context);
    }

    public SidePane(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SidePane(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** Attaches the pane to the main activity */
    public void attach(MainActivity activity) {
        mParent = activity;

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        mDrawer = activity.findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        initMenuItems();
    }

    /** Populates the menu with the user's personal data */
    public void displayUserData(GetAccountQuery.Account account) {
        TextView displayName = mParent.findViewById(R.id.header_display_name);
        displayName.setText(account.name());

        TextView email = mParent.findViewById(R.id.header_email);
        email.setText(account.email());

        MenuItem notebook = getMenu().findItem(R.id.notebook_menu_title);
        mNotebookMenu = new NotebookMenuManager(mParent, notebook.getSubMenu(), 3);
    }

    /** Initializes the static menu items */
    private void initMenuItems() {
        setNavigationItemSelectedListener(this::onNavigationItemSelected);
        MenuItem home = getMenu().getItem(0);
        home.setChecked(true);
        onNavigationItemSelected(home);
    }

    /** Loads a component's state into the main content frame once pressed */
    private boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                NotesFragment fragment = new MyNotesFragment();
                mParent.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                break;
            case R.id.nav_starred:
                // TODO: Load starred notes into content frame.
                break;
            case R.id.nav_explore:
                // TODO: Load explore section into content frame.
                break;
            default:
                mNotebookMenu.onItemSelected(item);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
