package com.marcusposey.notegala;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.marcusposey.notegala.net.ApolloQueryService;
import com.marcusposey.notegala.net.QueryService;

public class MainActivity extends AppCompatActivity {

    // Tag used for logging with android.util.Log
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Request code for starting SignInActivity for a result
    private static final int SIGN_IN_REQUEST = 1;

    private SidePane mSidePane;

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_REQUEST && resultCode == Activity.RESULT_OK) {
            String token = data.getStringExtra(SignInActivity.TOKEN_EXTRA);
            Log.i(LOG_TAG, "id token - " + token);

            new ApolloQueryService(token);
            // Best stick to the interface.
            QueryService.awaitInstance(service -> {
                service.getAccount((err, acct) -> {
                    if (err != null) {
                        Log.e(LOG_TAG, err.getMessage());

                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "network error",
                                    Toast.LENGTH_LONG).show();
                        });
                    } else {
                        mSidePane.displayUserData(acct);
                    }
                });
            });
        }
    }
}
