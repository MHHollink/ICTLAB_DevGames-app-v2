package nl.icode4living.devgames2.util;

import android.os.AsyncTask;

/**
 * A base class for asynchronous calls.
 *
 * @param <P>
 *         The type of parameters passed to the `doInBackground` method.
 * @param <I>
 *         The type of the increment (Integer usually).
 * @param <R>
 *         The type of the return value of the `doInBackground` method.
 */
public abstract class MultiThreadedAsyncTask<P, I, R> extends AsyncTask<P, I, R> {

    /**
     * Since pre-honeycomb SDK's only allow for a small amount of
     * simultaneous executed AsyncTasks, this method checks if we're
     * running on a more modern phone (SDK 11+), and if this is the
     * case, execute this task multi-threaded instead of serial.
     *
     * @param params
     *         The parameters of this task.
     */
    public final AsyncTask<P, I, R> executeThreaded(P... params) {
        return super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
}
