package nl.icode4living.devgames2;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import nl.icode4living.devgames2.connection.client.DevGamesClient;
import nl.icode4living.devgames2.connection.task.poll.PollMyUserTask;
import nl.icode4living.devgames2.event.BusProvider;
import nl.icode4living.devgames2.event.GoogleApiConnectionFailedEvent;
import nl.icode4living.devgames2.models.User;
import nl.icode4living.devgames2.util.L;
import nl.icode4living.devgames2.util.PreferenceManager;
import nl.icode4living.devgames2.util.Utils;
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class DevGamesApplication extends Application {

    /**
     * The key point to the key-value pair for the session in each request header
     */
    public static final String SESSION_HEADER_KEY = "SESSION_ID";
    /**
     * The value of the key-value pair in the Session Request header
     */
    private String session = null;

    private static final long DEFAULT_CONNECTION_TIMEOUT = 60 * 1000L;
    private static final long DEFAULT_READ_TIMEOUT = 60 * 1000L;

    /**
     * Instance of the preference manager that is used to access the {@link android.content.SharedPreferences}
     */
    private PreferenceManager preferenceManager;

    /**
     * Instance of the {@link DevGamesClient} that contains all the call to the REST api
     */
    private DevGamesClient devGamesClient;


    private GoogleApiClient devGamesGoogleApiClient;

    /**
     * Instance of a {@link User} that contains the information about the user that is currently logged in
     */
    private User loggedInUser;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate() {
        super.onCreate();

        L.v("Called");

        preferenceManager = PreferenceManager.get(this);

        devGamesClient = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (session != null && !session.isEmpty()) {
                            request.addHeader(SESSION_HEADER_KEY, session);
                        }
                    }
                })
                .setClient(new OkClient(
                        getOkHttpClient(
                                DEFAULT_CONNECTION_TIMEOUT,
                                DEFAULT_READ_TIMEOUT
                        )
                ))
                .setEndpoint(new Endpoint() {
                    @Override
                    public String getUrl() {
                        return BuildConfig.ENDPOINT_URL;
                    }

                    @Override
                    public String getName() {
                        return "devgames-backend";
                    }
                })
                .setConverter(new GsonConverter(
                                new GsonBuilder()
                                        .serializeNulls()
                                        .create()
                        )
                )
                .setLog(new AndroidLog("retrofit"))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(DevGamesClient.class);

        devGamesGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        L.d("Connected to google api");

                        Player p = Games.Players.getCurrentPlayer(devGamesGoogleApiClient);
                        String displayName;
                        if(p == null) {
                            displayName = "???";
                        } else {
                            displayName = p.getDisplayName();
                        }
                        Utils.createToast(getApplicationContext(), "Hello, "+displayName , Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        L.d("Connection suspended, trying again.");
                        devGamesGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        L.e("Connection failed: {0}", connectionResult);
                        BusProvider.getBus().post(new GoogleApiConnectionFailedEvent(connectionResult));

                    }
                })
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        loggedInUser = getUser();
        L.d("loaded app, loggedInUser={0}", loggedInUser);

    }


    /**
     * Gets the loggedInUser as {@link User} object from the app context
     *
     * @return currently logged in user
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Sets the loggedInUser as object in the app context for easier usage
     *
     * @param loggedInUser the new loggedInUser
     */
    public void setLoggedInUser(User loggedInUser) {
        L.v("loggedInUser={0}", loggedInUser != null ? loggedInUser.toString() : "null");
        this.loggedInUser = loggedInUser;
    }

    /**
    * Gets in instance of the PreferenceManager used in the app.
    * This preference manager contains calls used to and from the preferences and handles the tags used.
    *
    * @return Instance of the PreferenceManager
    */
    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    /**
     * Gets an instance of the DevGamesClient where all the REST calls are declared.
     *
     * @return instance of the DevGamesClient
     */
    public DevGamesClient getDevGamesClient() {
        return devGamesClient;
    }

    /**
     * @return The User, otherwise null if not found or when an error occurred
     */
    private User getUser() {
        if(loggedInUser == null) {
            new PollMyUserTask(this).executeThreaded();
        }
        return loggedInUser;
    }

    /**
     * Gets the current session for the REST calls.
     *
     * @return String value of Session Token
     */
    public String getSession() {
        return session;
    }

    /**
     * Sets the session which was returned from the server after login
     * Each request (expect /login) uses this session token for identification and authentication for the call
     *
     * @param session String value of the Session token
     */
    public void setSession(String session) {
        L.v("session={0}", session);
        this.session = session;
    }

    /**
     * Creates and returns an instance of the OkHttpClient used for the REST calls
     */
    private static OkHttpClient getOkHttpClient(long connectTimeoutMillis, long readTimeoutMillis) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS);
        client.setReadTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS);
        return client;
    }

    /**
     * Gets the instance of the DevGamesApplication by using the Context of the running activity/task.
     *
     * @param context Fragment which calls the method
     * @return instance of DevGamesApplication
     */
    public static DevGamesApplication get(Context context) {
        return (DevGamesApplication) context.getApplicationContext();
    }

    /**
     * Gets the instance of the DevGamesApplication by using the parent activity of the running fragment.
     *
     * @param context Fragment which calls the method
     * @return instance of DevGamesApplication
     */
    public static DevGamesApplication get(Fragment context) {
        return (DevGamesApplication) context.getActivity().getApplicationContext();
    }

    public GoogleApiClient getGoogleApiClient() {
        return devGamesGoogleApiClient;
    }
}
