package nl.icode4living.devgames2.connection.task;


import android.content.Context;

import nl.icode4living.devgames2.DevGamesApplication;
import nl.icode4living.devgames2.connection.client.DevGamesClient;
import nl.icode4living.devgames2.models.User;
import nl.icode4living.devgames2.util.MultiThreadedAsyncTask;
import nl.icode4living.devgames2.util.PreferenceManager;
import retrofit.RetrofitError;

/**
 * A base class responsible for making asynchronous REST calls to the DevGames
 * backend. All Tasks in this package that extend the RESTTask are therefor
 * asynchronous tasks.
 *
 * This class can be executed from a back- or foreground service, or a GUI
 * component, like an Activity or Fragment.
 *
 * @param <P>
 *         the type of the Parameter provided to this class.
 * @param <I>
 *         the Increment type. For example, if a progress bar needs to
 *         show the progress of this task, this type would probably be an
 *         integer (going from 0% to 100%).
 * @param <R>
 *         the Return type of this task.
 */
@SuppressWarnings("unused")
public abstract class RESTTask<P, I, R> extends MultiThreadedAsyncTask<P, I, R> {

    public static int SESSION_REFRESH_COUNTER = 0;
    private static final long LOADED = System.currentTimeMillis();

    private static final long DEFAULT_CONNECTION_TIMEOUT = 60 * 1000L;
    private static final long DEFAULT_READ_TIMEOUT = 60 * 1000L;

    /**
     * Some HTTP status codes.
     */
    public final static int OK = 200;
    public final static int CREATED = 201;
    public final static int NO_CONTENT = 204;
    public final static int BAD_REQUEST = 400;
    public final static int UNAUTHORIZED = 401;
    public final static int FORBIDDEN = 403;
    public final static int NOT_FOUND = 404;
    public final static int INTERNAL_SERVER_ERROR = 500;

    /**
     * Some local error codes
     */
    public final static int LOCAL_DB_ERROR = 1000;
    public final static int VIEW_NOT_VISIBLE_ANYMORE = 1001;
    public final static int GENERAL_CONNECTION_ERROR = 1002;
    public final static int LOCAL_FILE_SYSTEM_ERROR = 1003;
    public final static int CACHED_COPY_AVAILABLE = 1004;
    public final static int APP_INITIALIZING = 1005;
    public final static int APP_NOT_LOGGED_IN = 1006;
    public final static int APP_HAS_UN_SYNCHRONIZED_WORK = 1007;
    public final static int BACK_END_OFFLINE = 1008;

    /**
     * The context from which this task is started.
     */
    final Context context;

    /**
     * Creates a new instance of this REST task.
     *
     * @param context the context from which this task was created.
     */
    public RESTTask(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    /**
     * Creates an instance of a Retrofit service implementation. The
     * serviceClass-interface passed as a parameter are defined in the
     * child classes of this base REST class.
     *
     * @return an instance of a Retrofit service implementation.
     */
    public <T> DevGamesClient createService() {
        return getApplication().getDevGamesClient();
    }

    /**
     * Retrieves the HTTP status of a RetrofitError.
     *
     * @param error The error from which to retrieve the status.
     * @return the HTTP status of a RetrofitError.
     */
    public int getStatus(RetrofitError error) {
        if (error == null || error.getResponse() == null) {
            return GENERAL_CONNECTION_ERROR;
        }
        return error.getResponse().getStatus();
    }

    public void requestReLogin() {
        new LogoutTask(context, false).executeThreaded();
        PreferenceManager.clearAllSettings(context);
        DevGamesApplication.get(context).setLoggedInUser(null);
    }

    /**
     * Returns the {@link DevGamesApplication}.p
     *
     * @return The {@link DevGamesApplication}.
     */
    protected DevGamesApplication getApplication() {
        return DevGamesApplication.get(context);
    }

    protected User getLoggedInUser() {
        return getApplication().getLoggedInUser();
    }
}