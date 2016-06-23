package nl.icode4living.devgames2.connection.task;


import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import nl.icode4living.devgames2.DevGamesApplication;
import nl.icode4living.devgames2.event.BusProvider;
import nl.icode4living.devgames2.event.LogoutEvent;
import nl.icode4living.devgames2.models.User;
import nl.icode4living.devgames2.util.L;
import nl.icode4living.devgames2.util.PreferenceManager;

/**
 * An AsyncTask that logs out the user.
 */
public class LogoutTask extends RESTTask<Void, Void, Integer> {

    protected final boolean checkUnSyncedWork;

    /**
     * Creates a new instance of this REST task.
     *
     * @param context
     *         The context from which this task was created.
     * @param checkUnSyncedWork
     *         Whether the task should check if any model has un synchronized work left
     */
    public LogoutTask(Context context, boolean checkUnSyncedWork) {
        super(context);
        this.checkUnSyncedWork = checkUnSyncedWork;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        // Unregister from GCM, we should not get messages when logged out
        // The GCM key of the logged in user is also set to 'null' later on
        try {
            L.v("Trying to unregister from GCM");
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            gcm.unregister();
            L.i("Unregistered from GCM");
        }
        catch (IOException e) {
            L.e("Could not unregister from GCM");
        }

        DevGamesApplication app = DevGamesApplication.get(context);
        try {
            final User user = getLoggedInUser();
            User changes = new User();

            changes.setId(user.getId());
            changes.logout();

            user.logout();
        } catch (Exception e) {
            L.e("Could not save user state to the back-end!");
        }

        // Remove the session id from the application
        getApplication().setSession(null);
        getApplication().getGoogleApiClient().disconnect();

        PreferenceManager.clearAllSettings(context);

        return OK;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if (integer != null && integer == APP_HAS_UN_SYNCHRONIZED_WORK) {
            BusProvider.getBus().post(new LogoutEvent(true));
        }
        else {
            BusProvider.getBus().post(new LogoutEvent(false));
        }
    }
}