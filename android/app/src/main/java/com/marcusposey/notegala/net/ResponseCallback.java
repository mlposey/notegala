package com.marcusposey.notegala.net;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

/** Handles the response from an Apollo network request */
class ResponseCallback<T, S> extends ApolloCall.Callback<T> {
    @FunctionalInterface
    public interface ResponseListener <T> {
        void onResponse(@Nonnull Response<T> response);
    }

    private final ResponseListener<T> mResponse;
    private final QueryService.Listener<S> mOnFailure;
    private final String mErrMsg;

    /**
     * Creates a ResponseCallback capable of handling both successful and failed responses
     *
     * @param resp Acts on the raw GraphQL response from the server
     * @param onFailure Called if the network request fails or the raw response is null
     * @param errMsg Supplied to the onFailure#onResult method if the raw response is null
     */
    public ResponseCallback(ResponseListener<T> resp, QueryService.Listener<S> onFailure, String errMsg) {
        mResponse = resp;
        mOnFailure = onFailure;
        mErrMsg = errMsg;
    }

    @Override
    public void onResponse(@Nonnull Response<T> response) {
        if (response.data() == null) {
            mOnFailure.onResult(new Exception(mErrMsg), null);
        } else {
            mResponse.onResponse(response);
        }
    }

    @Override
    public void onFailure(@Nonnull ApolloException e) { mOnFailure.onResult(e, null); }
}
