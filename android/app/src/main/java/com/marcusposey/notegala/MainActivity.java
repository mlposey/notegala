package com.marcusposey.notegala;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.marcusposey.notegala.net.ApolloQueryService;
import com.marcusposey.notegala.net.QueryService;
import com.marcusposey.notegala.notebook.NotebookNotesFragment;
import com.marcusposey.notegala.search.SearchActivity;

/** Manages the root app state and initial Google token acquisition through SignInActivity */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    
    // Tag used for logging with android.util.Log
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Request code for starting SignInActivity for a result
    private static final int SIGN_IN_REQUEST = 1;

    // The side navigation bar that manages major app components
    private SidePane mSidePane;

    // The search item on the app bar
    private @Nullable MenuItem mSearchItem;

    /** Configures the side pane and starts an asynchronous SignInActivity request */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SignInActivity.class);
        startActivityForResult(intent, SIGN_IN_REQUEST);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSidePane = findViewById(R.id.nav_view);
        mSidePane.attach(this);
    }

    /** Closes the side pane if it is open */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    /** Configures the search menu item */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchItem = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) mSearchItem.getActionView();
        ComponentName searchComponent = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(searchComponent));
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSearchItem != null) {
            SearchView searchView = (SearchView) mSearchItem.getActionView();
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
    }

    /**
     * Uses the token result from SignInActivity to build a QueryService used to
     * request the user's account information.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_REQUEST && resultCode == Activity.RESULT_OK) {
            String token = data.getStringExtra(SignInActivity.TOKEN_EXTRA);
            Log.i(LOG_TAG, "id token - " + token);

            new ApolloQueryService(token);
            QueryService.awaitInstance(service -> {
                service.getAccount((err, acct) -> {
                    runOnUiThread(() -> {
                        if (err != null) {
                            Log.e(LOG_TAG, err.getMessage());
                                Toast.makeText(getApplicationContext(),
                                        getText(R.string.network_err), Toast.LENGTH_LONG).show();
                        } else {
                            mSidePane.displayUserData(acct);
                        }
                    });
                });
            });
        }
    }

    /** Sends additional query context information if on a NotebookNotesFragment */
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onQueryTextSubmit(String query) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof NotebookNotesFragment) {
            Bundle bundle = new Bundle();
            bundle.putString(SearchActivity.NOTEBOOK_ID, ((NotebookNotesFragment) fragment).getNotebookId());
            ((SearchView) mSearchItem.getActionView()).setAppSearchData(bundle);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
