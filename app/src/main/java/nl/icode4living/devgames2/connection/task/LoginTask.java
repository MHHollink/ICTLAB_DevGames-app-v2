package nl.icode4living.devgames2.connection.task;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */

import android.content.Context;
import android.content.res.Resources;

import java.util.Map;

import nl.icode4living.devgames2.DevGamesApplication;
import nl.icode4living.devgames2.connection.client.DevGamesClient;
import nl.icode4living.devgames2.event.BusProvider;
import nl.icode4living.devgames2.event.LoginEvent;
import nl.icode4living.devgames2.models.User;
import nl.icode4living.devgames2.util.L;
import retrofit.RetrofitError;

/**
 * An asynchronous task performing the login of a user.
 */
public class LoginTask extends RESTTask< Void, Void, Integer> {

    // The prefix of the message identifier (see onPostExecute(...))
    private static final String ID_PREFIX = "login_";

    // The resource type of the message identifier (see onPostExecute(...))
    private static final String ID_TYPE = "string";

    // The username.
    private final String username;

    // The plain text password.
    private final String password;

    /**
     * Creates a new LoginTask.
     *
     * @param context
     *         the context from which this task is called.
     * @param username
     *         the username.
     * @param password
     *         the password.
     */
    public LoginTask(Context context, String username, String password) {
        super(context);
        this.username = username;
        this.password = password;
    }


    @Override
    protected Integer doInBackground(Void... params) {

        DevGamesApplication application = (DevGamesApplication) context.getApplicationContext();

        DevGamesClient client = super.createService();

        final String session;

        // First try to get the session ID.
        try {
            Map<String, String> map = client.login(username, password);
            session = map.get(DevGamesApplication.SESSION_HEADER_KEY);

            if (session == null) {
                L.wtf("this should never happen: response {0} returned OK " +
                        "but does not contain {1}", map, DevGamesApplication.SESSION_HEADER_KEY);
                return INTERNAL_SERVER_ERROR;
            }
        }
        catch (RetrofitError e) {
            return super.getStatus(e);
        }
        catch (NullPointerException e) {
            return RESTTask.BACK_END_OFFLINE;
        }

        L.d("retrieved session: {0}", session);
        application.setSession(session);

        // Get your user object.
        try {
            User user = client.getCurrentlyLoggedInUser();

            if (user == null) {
                L.wtf("This should never happen! User from freshly gotten session is null.");
                return INTERNAL_SERVER_ERROR;
            }

            application.setLoggedInUser(user);
            application.getPreferenceManager().setLastUsedUsername(user.getUsername());
            application.getPreferenceManager().setLastPassword(password);

        }
        catch (RetrofitError e) {
            return super.getStatus(e);
        }
        catch (NullPointerException e) {
            return RESTTask.BACK_END_OFFLINE;
        }
        L.d("successfully logged in user: {0}", getLoggedInUser().getId());

        return OK;
    }

    @Override
    protected void onPostExecute(Integer httpStatus) {

        L.d("onPostExecute, httpStatus={0}", httpStatus);

        Resources resources = this.context.getResources();
        boolean success = (httpStatus == OK);
        String message = null;

        // See if there's a predefined message for `httpStatus`.
        int messageId = resources.getIdentifier(ID_PREFIX + httpStatus,
                ID_TYPE, context.getPackageName());

        if (!success && messageId > 0) {
            message = resources.getString(messageId);
        }

        BusProvider.getBus().post(new LoginEvent(success, message));
    }
}