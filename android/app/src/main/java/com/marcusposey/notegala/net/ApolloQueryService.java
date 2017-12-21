package com.marcusposey.notegala.net;

import com.apollographql.apollo.ApolloClient;
import com.marcusposey.notegala.net.gen.GetAccountQuery;

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


    @Override
    public void getAccount(Listener<GetAccountQuery.Account> listener) {
        // TODO: Perform an account query.
    }
}