package nl.icode4living.devgames2.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import nl.icode4living.devgames2.DevGamesApplication;
import nl.icode4living.devgames2.R;
import nl.icode4living.devgames2.connection.task.LogoutTask;
import nl.icode4living.devgames2.event.BusProvider;
import nl.icode4living.devgames2.event.LogoutEvent;
import nl.icode4living.devgames2.util.L;
import nl.icode4living.devgames2.util.PreferenceManager;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public abstract class DevGamesActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ProgressDialog logoutProgressDialog;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        L.d("onCreate");

        preferenceManager = PreferenceManager.get(this);

        doLoginCheck();

        logoutProgressDialog = new ProgressDialog(this);
        logoutProgressDialog.setMessage(getString(R.string.logging_out));
        logoutProgressDialog.setIndeterminate(true);
        logoutProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    /**
     * Called when Android resumes this Activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        L.d("* onResume");

        doLoginCheck();

        BusProvider.getBus().register(this);
    }

    /**
     * Called when Android pauses this Activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        L.d("* onPause");

        BusProvider.getBus().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        L.d("* onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        if (i == R.id.menu_logout) {
            doLogout(true);
        } else if (i == android.R.id.home) {
            onBackPressed();
        } else {
            return false;
        }

        return true;
    }

    private void doLogout(boolean checkUnSyncedWork) {
        logoutProgressDialog.show();

        new LogoutTask(this, checkUnSyncedWork).executeThreaded();
    }

    protected void doLoginCheck() {
        if (!isLoggedIn()) {
            L.i("not logged in yet, starting LoginActivity");

            // If the user is not logged in, start the login Activity.
            startActivity(new Intent(this, LoginActivity.class));

            // Call finish() and return so that the back button cannot
            // take the user back to this activity.
            finish();
        }
    }

    /**
     * Returns true iff the user is logged in. I.e. if the session
     * id is not empty, or null.
     *
     * @return true iff the user is logged in.
     */
    public boolean isLoggedIn() {
        L.d("* isLoggedIn");
        return getDevGamesApplication().getLoggedInUser() != null;
    }

    @Subscribe
    public void onLogoutEvent(LogoutEvent event) {

        logoutProgressDialog.dismiss();

        if (event.hasUnSynchronizedWork) {
            L.i("LogoutEvent received, but their is still un synchronized work in the database. Logout cancelled");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.logout)
                    .setMessage("Er zijn nog items op dit apparaat die niet geschynchroniseerd zijn met de server. \n" +
                            "Als u uitlogt, worden de gegevens niet op de achtergrond gesynchroniseerd, tenzij u opnieuw inlogt.\n" +
                            "Als een andere gebruiker ondertussen inlogt, gaan uw gesynchroniseerde gegevens verloren. \n\nNu uitloggen?")
                    .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doLogout(false);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            L.i("LogoutEvent received. Goodbye!");
            DevGamesApplication.get(this).setLoggedInUser(null);

            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * Returns the {@link PreferenceManager} for this app. This is meant for retrieval and saving
     * small preferences in the {@link android.content.SharedPreferences}. This is initialized in {@link
     * #onCreate(android.os.Bundle)}.
     *
     * @return The {@link PreferenceManager} for this app
     */
    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public DevGamesApplication getDevGamesApplication() {
        return (DevGamesApplication) getApplication();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
