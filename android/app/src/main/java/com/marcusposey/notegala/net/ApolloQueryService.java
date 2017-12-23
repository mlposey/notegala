package com.marcusposey.notegala.net;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.marcusposey.notegala.net.gen.CreateNoteMutation;
import com.marcusposey.notegala.net.gen.GetAccountQuery;
import com.marcusposey.notegala.net.gen.NewNoteInput;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/** Makes queries to the remote GraphQL API */
public class ApolloQueryService extends QueryService {
    private static final String SERVER_URL = "http://api.marcusposey.com:9002/graphql";

    private final ApolloClient mApolloClient;

    /**
     * Configures the service to make authenticated requests against the API
     * @param idToken A Google Id token to pass using bearer authentication
     */
    public ApolloQueryService(String idToken) {
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

        // Start providing this object to callers of super.awaitInstance(...).
        emitInstance();
    }


    /**
     * Fetches data relating to the user account
     *
     * This method corresponds to the 'account' query in the core API's
     * GraphQL spec.
     */
    @Override
    public void getAccount(Listener<GetAccountQuery.Account> listener) {
        mApolloClient.query(GetAccountQuery.builder().build()).enqueue(new ApolloCall.Callback<GetAccountQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<GetAccountQuery.Data> response) {
                if (response.data() == null) {
                    listener.onResult(new Exception("missing account data"), null);
                } else {
                    listener.onResult(null, response.data().account());
                }
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                listener.onResult(e, null);
            }
        });
    }

    /**
     * Uploads a new note to the server
     *
     * This method corresponds to the 'createNote' mutation in the core API's
     * GraphQL spec.
     */
    @Override
    public void createNote(NewNoteInput input, Listener<CreateNoteMutation.Note> listener) {
        mApolloClient.mutate(CreateNoteMutation.builder().input(input).build())
                .enqueue(new ApolloCall.Callback<CreateNoteMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<CreateNoteMutation.Data> response) {
                if (response.data() == null) {
                    listener.onResult(new Exception("could not upload note"), null);
                } else {
                    listener.onResult(null, response.data().note());
                }
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) { listener.onResult(e, null); }
        });
    }
}