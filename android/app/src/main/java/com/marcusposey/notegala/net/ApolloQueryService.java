package com.marcusposey.notegala.net;

import com.apollographql.apollo.ApolloClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.marcusposey.notegala.SignInActivity;
import com.marcusposey.notegala.net.gen.mutation.CreateNoteMutation;
import com.marcusposey.notegala.net.gen.mutation.CreateNotebookMutation;
import com.marcusposey.notegala.net.gen.query.SearchQuery;
import com.marcusposey.notegala.net.gen.type.EditNoteInput;
import com.marcusposey.notegala.net.gen.mutation.EditNoteMutation;
import com.marcusposey.notegala.net.gen.type.EditNotebookInput;
import com.marcusposey.notegala.net.gen.mutation.EditNotebookMutation;
import com.marcusposey.notegala.net.gen.query.GetAccountQuery;
import com.marcusposey.notegala.net.gen.query.MyNotebooksHeadQuery;
import com.marcusposey.notegala.net.gen.query.MyNotesQuery;
import com.marcusposey.notegala.net.gen.type.NewNoteInput;
import com.marcusposey.notegala.net.gen.fragment.Note;
import com.marcusposey.notegala.net.gen.query.NotebookQuery;
import com.marcusposey.notegala.net.gen.mutation.RemoveNoteMutation;
import com.marcusposey.notegala.net.gen.mutation.RemoveNotebookMutation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/** Makes queries to the remote GraphQL API */
public class ApolloQueryService extends QueryService {
    /** Network response types */
    public enum ResponseType {
        NOTE_CHANGE,
        NOTEBOOK_DELETE,
        NOTEBOOK_RENAME
    }

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
     * This method corresponds to the 'notes' query in the core API's
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
                        notifyObservers(ResponseType.NOTE_CHANGE);
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
                        notifyObservers(ResponseType.NOTE_CHANGE);
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
                        notifyObservers(ResponseType.NOTE_CHANGE);
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

    /**
     * Removes the notebook from the user's collection, notifying observers if the request succeeds
     *
     * This method corresponds to the 'removeNotebook' mutation in the core API's
     * GraphQL spec.
     * @param id The unique id of the notebook
     */
    @Override
    public void removeNotebook(String id, Listener<Boolean> listener) {
        checkTokenThen(() -> {
            mApolloClient.mutate(RemoveNotebookMutation.builder().id(id).build()).enqueue(
                    new ResponseCallback<>(response -> {
                        listener.onResult(null, response.data().wasRemoved());
                        setChanged();
                        notifyObservers(ResponseType.NOTEBOOK_DELETE);
                    }, listener, "could not delete notebook")
            );
        });
    }

    /**
     * Uploads modified notebook content to the server, notifying observers if the request succeeds
     *
     * This method corresponds to the 'editNotebook' mutation in the core API's
     * GraphQL spec.
     */
    @Override
    public void editNotebook(EditNotebookInput input, Listener<EditNotebookMutation.Notebook> listener) {
        checkTokenThen(() -> {
            mApolloClient.mutate(EditNotebookMutation.builder().input(input).build()).enqueue(
                    new ResponseCallback<>(response -> {
                        listener.onResult(null, response.data().notebook());
                        setChanged();
                        notifyObservers(ResponseType.NOTEBOOK_RENAME);
                    }, listener, "could not upload edited notebook")
            );
        });
    }

    /**
     * Searches the user's notes according to a query
     *
     * This method corresponds to the 'search' query in the core API' GraphQL spec.
     * @param query Phrase or keywords to search for
     * @param notebookId Optional notebook id to restrict the search scope
     */
    @Override
    public void search(String query, @Nullable String notebookId, Listener<List<SearchQuery.Match>> listener) {
        checkTokenThen(() -> {
            mApolloClient.query(SearchQuery.builder().query(query).notebook(notebookId).build()).enqueue(
                    new ResponseCallback<>(
                        response -> listener.onResult(null, response.data().matches()),
                        listener, "could not get response")
            );
        });
    }
}