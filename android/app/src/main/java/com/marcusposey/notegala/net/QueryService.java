package com.marcusposey.notegala.net;

import com.marcusposey.notegala.net.gen.CreateNoteMutation;
import com.marcusposey.notegala.net.gen.GetAccountQuery;
import com.marcusposey.notegala.net.gen.MyNotesQuery;
import com.marcusposey.notegala.net.gen.NewNoteInput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

/**
 * Models a service that makes network queries
 *
 * There should be only one QueryService implementation in the application. This abstraction exists
 * to:
 * - simplify mock creation for testing
 * - synchronize instances
 */
public abstract class QueryService {

    /** Waits for the QueryService to become configured */
    @FunctionalInterface
    public interface InstanceListener {
        void onConfigured(QueryService service);
    }

    /** Accepts the result/response of a network request */
    @FunctionalInterface
    public interface Listener <T> {
        void onResult(@Nullable Exception e, @Nullable T response);
    }

    private static QueryService sInstance;
    private static ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static List<InstanceListener> mObservers = new ArrayList<>();

    /**
     * Adds a listener to the waiting queue
     * @param listener Will be supplied a QueryService object once one is created
     *                 or instantly given an existing one if available
     */
    public static final void awaitInstance(InstanceListener listener) {
        if (sInstance == null) {
            mObservers.add(listener);
        } else {
            listener.onConfigured(sInstance);
        }
    }

    /** Sends an instance of this object to all current and future InstanceListeners */
    protected final void emitInstance() {
        sInstance = this;
        for (InstanceListener listener : mObservers) {
            sExecutor.submit(() -> listener.onConfigured(this));
        }
        mObservers.clear();
    }

    public abstract void getAccount(Listener<GetAccountQuery.Account> listener);
    public abstract void getMyNotes(Listener<List<MyNotesQuery.Note>> listener);
    public abstract void createNote(NewNoteInput input, Listener<CreateNoteMutation.Note> listener);
}
