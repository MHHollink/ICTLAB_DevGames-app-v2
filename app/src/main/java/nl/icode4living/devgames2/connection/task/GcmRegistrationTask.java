package nl.icode4living.devgames2.connection.task;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import nl.icode4living.devgames2.BuildConfig;
import nl.icode4living.devgames2.DevGamesApplication;
import nl.icode4living.devgames2.models.User;
import nl.icode4living.devgames2.util.L;

/**
 * Checks if the logged in user is registered at Google Cloud Messaging. If not, it tries to register. The registration
 * key is saved in {@link User#gcmId}.
 */
public class GcmRegistrationTask extends RESTTask<Void, Void, String> {

    public static final String REGISTERED = "registered";
    public static final String ERROR_USER_NOT_AVAILABLE = "error_user_not_available";
    public static final String ERROR_DATABASE_ERROR = "error_database_error";
    public static final String ERROR_MISSING_GCM_SENDER_ID = "error_missing_gcm_sender_id";
    public static final String ERROR_PLAY_SERVICES_NOT_AVAILABLE = "error_play_services_not_available";
    public static final String ERROR_GCM_REGISTER_ERROR = "error_gcm_register_error";
    public static final String ERROR_NOT_LOGGED_IN = "error_not_logged_in";

    private String newGCMKey = null;
    private String gcmError = null;
    private User loggedInUser;


    public GcmRegistrationTask(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(Void[] params) {

        if (!checkPlayServices()) {
            return ERROR_PLAY_SERVICES_NOT_AVAILABLE;
        }

        loggedInUser = getLoggedInUser();

        if (loggedInUser == null) {
            L.e("Cannot determine if registered at GCM. User is null");
            return ERROR_USER_NOT_AVAILABLE;
        }

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        try {
            newGCMKey = gcm.register(BuildConfig.GCM_SENDER_ID);
            L.d("Registered to GCM, GCMKey : {0}", newGCMKey);

        }
        catch (IOException e) {

            L.e("Looks like Google Play Services is not available at this moment. Cannot register at GCM.");
            gcmError = e.getMessage();

            return ERROR_GCM_REGISTER_ERROR;
        }

        loggedInUser.setGcmId(newGCMKey);
        createService().updateUser(loggedInUser, DevGamesApplication.get(context).getLoggedInUser().getId() );

        return REGISTERED;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        L.v("called");

        if (loggedInUser == null) {
            L.d("Logged in user is null");
            return;
        }

        String oldKey = loggedInUser.getGcmId();

        // Only update if the new key is not null or empty, and the new key does not equal the old key
        if (!TextUtils.isEmpty(newGCMKey) && !newGCMKey.equals(oldKey)) {

            L.v("Saving new GCM key!");
            loggedInUser.setGcmId(newGCMKey);

        } else {
            L.i("New GCM key is null, error code: {0}, error from Google Play Services: {1}", result, gcmError);
            // TODO: reschedule an update?
        }
    }

    protected boolean checkPlayServices() {

        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else {
            gcmError = GooglePlayServicesUtil.getErrorString(result);
            return false;
        }
    }
}