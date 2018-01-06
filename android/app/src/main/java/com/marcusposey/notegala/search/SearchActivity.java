package com.marcusposey.notegala.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.marcusposey.notegala.R;
import com.marcusposey.notegala.net.QueryService;

/** Handles search queries for notes */
public class SearchActivity extends AppCompatActivity {
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    // Holds the search results in the results frame
    private ResultsFragment mResultsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setSupportActionBar(findViewById(R.id.search_toolbar));

        mResultsFragment = new ResultsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.results_frame, mResultsFragment)
                .commit();

        handleIntent(getIntent());
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
                        mResultsFragment.showResults(matches);
                    }
                });
            });
        });
    }
}
