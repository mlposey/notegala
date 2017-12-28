package com.marcusposey.notegala;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Scope;

/**
 * Acquires a Google Id token from the user
 *
 * This activity can be started for a result. That process attempts
 * to acquire the token silently at first, then explicitly through
 * a button press if silent sign in fails. The resulting intent holds
 * the Id token as a string under the key SignInActivity.TOKEN_EXTRA.
 */
public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    /** Denotes the method used to carry out the sign in process */
    private enum Method { SILENT, EXPLICIT }

    private static final String LOG_TAG = SignInActivity.class.getSimpleName();

    // The result key for an Id token
    public static final String TOKEN_EXTRA = "TOKEN_EXTRA";

    // Request code for a Google Sign-In activity result
    private static final int RC_SIGN_IN = 9001;

    private static GoogleApiClient sGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (sGoogleApiClient == null) {
            sGoogleApiClient = createApiClient();
        }
        signIn(Method.SILENT);
    }

    /**
     * Signs in using a Google account
     * @param context Controls the process visibility.
     *                - Method.SILENT attempts to sign in without prompting the user
     *                  for access. It reduces to an Method.EXPLICIT action
     *                  upon failure.
     *                - Method.EXPLICIT attempts to sign in by prompting the user
     *                  for access. The app will become stuck on this activity
     *                  if explicit sign in fails.
     */
    private void signIn(final Method context) {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(sGoogleApiClient);

        if (opr != null && context == Method.SILENT) {
            // Try silent sign in.
            handleGooglePendingResult(opr);
        }
        else {
            // Try explicit sign in.
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(sGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result, Method.EXPLICIT);
        }
    }

    private void handleSignInResult(GoogleSignInResult result, final Method context) {
        if (result != null && result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            Intent intent = new Intent();
            intent.putExtra(TOKEN_EXTRA, acct.getIdToken());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else if (result != null && context == Method.SILENT) {
            Log.i(LOG_TAG, "failed silent sign in");
            enableSignInButton();
        }
        else {
            Log.e(LOG_TAG, "sign in failed; err " +
                    result.getStatus().getStatusCode());
            Toast.makeText(getApplicationContext(), "sign in failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void enableSignInButton() {
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener((view) -> signIn(Method.EXPLICIT));
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setVisibility(View.VISIBLE);
    }

    private void handleGooglePendingResult(OptionalPendingResult<GoogleSignInResult> pendingResult) {
        if (pendingResult.isDone()) {
            GoogleSignInResult signInResult = pendingResult.get();
            handleSignInResult(signInResult, Method.SILENT);
        } else {
            // It's okay if it isn't done right away. Handle it when it's available.
            pendingResult.setResultCallback((result) -> handleSignInResult(result, Method.SILENT));
        }
    }

    /**
     * Creates a GoogleApiClient for the sign in process
     *
     * @return A client with access to an email and Id token
     */
    private GoogleApiClient createApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /** Called if sGoogleApiClient cannot establish a connection */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "no connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {}

    /** Returns the Google API Client used to complete sign in */
    public static GoogleApiClient getApiClient() {
        return sGoogleApiClient;
    }
}