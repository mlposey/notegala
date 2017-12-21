package com.marcusposey.notegala;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;

/** Processes events related to the left navigation drawer */
public class SidePane extends NavigationView {
    private DrawerLayout mDrawer;

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
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        mDrawer = activity.findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home: break;
            case R.id.nav_starred: break;
            case R.id.nav_explore: break;
            case R.id.nav_misc: break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
