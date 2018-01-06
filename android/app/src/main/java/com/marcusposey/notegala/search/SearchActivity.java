package com.marcusposey.notegala.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.ApolloQueryService;
import com.marcusposey.notegala.net.QueryService;

import java.util.Observable;
import java.util.Observer;

/** Handles search queries for notes */
public class SearchActivity extends AppCompatActivity implements Observer {
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    // Holds the search results in the results frame
    private ResultsFragment mResultsFragment;

    // The last query that was submitted
    private String mLastQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setSupportActionBar(findViewById(R.id.search_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mResultsFragment = new ResultsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.results_frame, mResultsFragment)
                .commit();

        QueryService.awaitInstance(service -> service.addObserver(this));

        handleIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        QueryService.awaitInstance(service -> service.deleteObserver(this));
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchNotes(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    /** Finds personal notes that match the specified query */
    private void searchNotes(String query) {
        Log.i(LOG_TAG, "query: " + query);

        QueryService.awaitInstance(service -> {
            service.search(query, null, (err, matches) -> {
                runOnUiThread(() -> {
                    if (err != null) {
                        Log.e(LOG_TAG, err.getMessage());
                        Toast.makeText(getApplicationContext(), getString(R.string.network_err),
                                Toast.LENGTH_LONG).show();
                    } else {
                        mLastQuery = query;
                        mResultsFragment.showResults(matches);
                    }
                });
            });
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof QueryService && arg instanceof ApolloQueryService.ResponseType &&
                arg == ApolloQueryService.ResponseType.NOTE_CHANGE) {
            if (mLastQuery != null) searchNotes(mLastQuery);
        }
    }
}
