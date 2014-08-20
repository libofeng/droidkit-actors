package com.droidkit.actors.tasks;

import java.util.LinkedList;

/**
 * Modified future for ask pattern of Actors
 * Created to work only in actor threads
 */
public class AskFuture<T> {

    private LinkedList<AskCallback> callbacks = new LinkedList<AskCallback>();

    private ActorAskImpl askImpl;
    private int reqId;

    private boolean isCompleted = false;
    private boolean isCanceled = false;
    private boolean isError = false;
    private T result = null;
    private Throwable error = null;

    AskFuture(ActorAskImpl askImpl, int reqId) {
        this.askImpl = askImpl;
        this.reqId = reqId;
    }

    public void addListener(AskCallback callback) {
//        if (isCompleted) {
//            if (isCanceled) {
//                callback.onError(new AskCancelled());
//            } else if (isError) {
//                callback.onError(error);
//            } else {
//                callback.onResult(result);
//            }
//        } else {
//            callbacks.add(callback);
//        }
        callbacks.add(callback);
    }

    public void removeListener(AskCallback callback) {
        callbacks.remove(callback);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isError() {
        return isError;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public Throwable error() {
        return error;
    }

    public T result() {
        return result;
    }

    void onError(Throwable throwable) {
        if (isCompleted) {
            return;
        }
        isCompleted = true;
        isCanceled = false;
        isError = true;
        error = throwable;
        result = null;

        for (AskCallback callback : callbacks) {
            callback.onError(throwable);
        }
    }

    void onResult(T res) {
        if (isCompleted) {
            return;
        }
        isCompleted = true;
        isCanceled = false;
        isError = false;
        error = null;
        result = res;

        for (AskCallback callback : callbacks) {
            callback.onResult(res);
        }
    }

    void onTimeout() {
        onError(new AskTimeoutException());
    }
}
