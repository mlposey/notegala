package com.marcusposey.notegala.net;

import com.apollographql.apollo.ApolloClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.marcusposey.notegala.SignInActivity;
import com.marcusposey.notegala.net.gen.CreateNoteMutation;
import com.marcusposey.notegala.net.gen.CreateNotebookMutation;
import com.marcusposey.notegala.net.gen.EditNoteInput;
import com.marcusposey.notegala.net.gen.EditNoteMutation;
import com.marcusposey.notegala.net.gen.GetAccountQuery;
import com.marcusposey.notegala.net.gen.MyNotebooksHeadQuery;
import com.marcusposey.notegala.net.gen.MyNotesQuery;
import com.marcusposey.notegala.net.gen.NewNoteInput;
import com.marcusposey.notegala.net.gen.Note;
import com.marcusposey.notegala.net.gen.NotebookQuery;
import com.marcusposey.notegala.net.gen.RemoveNoteMutation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/** Makes queries to the remote GraphQL API */
public class ApolloQueryService extends QueryService {
    private static final String SERVER_URL = "http://api.marcusposey.com:9002/graphql";

    private ApolloClient mApolloClient;

    private final ExecutorService mService = Executors.newCachedThreadPool();

    // The time in milliseconds that a Google Id token lasts
    private static final long TOKEN_DURATION = 1000 * 3600;

    // The time at which the Google Id token in use will expire
    private long mTokenExpiration;

    /**
     * Configures the service to make authenticated requests against the API
     * @param idToken A Google Id token to pass using bearer authentication
     */
    public ApolloQueryService(String idToken) {
        mTokenExpiration = System.currentTimeMillis() + TOKEN_DURATION;
        buildApolloClient(idToken);

        // Start providing this object to callers of super.awaitInstance(...).
        emitInstance();
    }

    private void buildApolloClient(String idToken) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .method(original.method(),original.body());
                    builder.header("Authorization", "Bearer " + idToken);
                    return chain.proceed(builder.build());
                })
                .build();

        mApolloClient = ApolloClient.builder()
                .serverUrl(SERVER_URL)
                .okHttpClient(httpClient)
                .build();
    }

    /**
     * Ensures the Google Id token is still valid and refreshes it if needed
     * 
     * If the token must be refreshed, it is done on a separate thread where
     * after will also be executed.
     *
     * @param after Called after the check, even if refresh fails
     */
    private void checkTokenThen(Runnable after) {
        if (System.currentTimeMillis() < mTokenExpiration) {
            after.run();
            return;
        }

        mService.submit(() -> {
            GoogleApiClient client = SignInActivity.getApiClient();
            ConnectionResult conResult = client.blockingConnect();

            if (conResult.isSuccess()) {
                GoogleSignInResult signInResult = Auth.GoogleSignInApi.silentSignIn(client).await();
                buildApolloClient(signInResult.getSignInAccount().getIdToken());
                mTokenExpiration = System.currentTimeMillis() + TOKEN_DURATION;
            }
            after.run();
        });
    }

    /**
     * Fetches data relating to the user account
     *
     * This method corresponds to the 'account' query in the core API's
     * GraphQL spec.
     */
    @Override
    public void getAccount(Listener<GetAccountQuery.Account> listener) {
        checkTokenThen(() -> {
            mApolloClient.query(GetAccountQuery.builder().build()).enqueue(new ResponseCallback<>(
                    response -> listener.onResult(null, response.data().account()),
                    listener, "missing account data"
            ));
        });
    }

    /**
     * Fetches all notes owned by the user
     *
     * This method corresponds to the 'myNotes' query in the core API's
     * GraphQL spec
     */
    @Override
    public void getMyNotes(Listener<List<Note>> listener) {
        checkTokenThen(() -> {
            mApolloClient.query(MyNotesQuery.builder().build()).enqueue(new ResponseCallback<>(
                    response -> listener.onResult(null, response.data().notes()),
                    listener, "failed to retrieve notes"
            ));
        });
    }

    /**
     * Uploads a new note to the server and notifies observers if the request succeeds
     *
     * This method corresponds to the 'createNote' mutation in the core API's
     * GraphQL spec.
     */
    @Override
    public void createNote(NewNoteInput input, Listener<CreateNoteMutation.Note> listener) {
        checkTokenThen(() -> {
            mApolloClient.mutate(CreateNoteMutation.builder().input(input).build()).enqueue(
                    new ResponseCallback<>(response -> {
                        listener.onResult(null, response.data().note());
                        setChanged();
                        notifyObservers();
                    }, listener, "could not upload note")
            );
        });
    }

    /**
     * Uploads modified note content to the server and notifies observers if the request succeeds
     *
     * This method corresponds to the 'editNote' mutation in the core API's
     * GraphQL spec.
     */
    @Override
    public void editNote(EditNoteInput input, Listener<EditNoteMutation.Note> listener) {
        checkTokenThen(() -> {
            mApolloClient.mutate(EditNoteMutation.builder().input(input).build()).enqueue(
                    new ResponseCallback<>(response -> {
                        listener.onResult(null, response.data().note());
                        setChanged();
                        notifyObservers();
                    }, listener, "could not upload edited note")
            );
        });
    }

    /**
     * Removes the note from the user's collection and notifies observers if the request succeeds
     *
     * This method corresponds to the 'removeNote' mutation in the core API's
     * GraphQL spec.
     * @param id The unique id of the note
     */
    @Override
    public void removeNote(String id, Listener<Boolean> listener) {
        checkTokenThen(() -> {
            mApolloClient.mutate(RemoveNoteMutation.builder().id(id).build()).enqueue(
                    new ResponseCallback<>(response -> {
                        listener.onResult(null, response.data().removeNote());
                        setChanged();
                        notifyObservers();
                    }, listener, "could not delete note")
            );
        });
    }

    /**
     * Gets header information for each notebook owned by the user
     *
     * This does not retrieve the notes of each, as is possible with the full
     * 'myNotebooks' GraphQL query.
     */
    @Override
    public void getNotebookHeaders(Listener<List<MyNotebooksHeadQuery.Notebook>> listener) {
        checkTokenThen(() -> {
            mApolloClient.query(MyNotebooksHeadQuery.builder().build()).enqueue(new ResponseCallback<>(
                    response -> listener.onResult(null, response.data().notebooks()),
                    listener, "could not retrieve notebook headers")
            );
        });
    }

    /**
     * Gets all notes that belong to a notebook
     *
     * @param id The unique id of the notebook
     */
    @Override
    public void getNotebookNotes(String id, Listener<List<Note>> listener) {
        checkTokenThen(() -> {
            mApolloClient.query(NotebookQuery.builder().id(id).build()).enqueue(new ResponseCallback<>(
                    response -> listener.onResult(null, response.data().notebook().notes()),
                    listener, "could not retrieve notebook notes"
            ));
        });
    }

    /**
     * Creates a new notebook having the specified title
     *
     * This method corresponds to the 'createNotebook' mutation in the core API's
     * GraphQL spec.
     */
    @Override
    public void createNotebook(String title, Listener<CreateNotebookMutation.Notebook> listener) {
        checkTokenThen(() -> {
            mApolloClient.mutate(CreateNotebookMutation.builder().title(title).build()).enqueue(
                    new ResponseCallback<>(response -> {
                        listener.onResult(null, response.data().notebook());
                        setChanged();
                        notifyObservers(response.data().notebook());
                    }, listener, "could not create notebook")
            );
        });
    }
}