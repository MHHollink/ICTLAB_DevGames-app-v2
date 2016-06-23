package nl.icode4living.devgames2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

/**
 * A manager cl`s that is responsible for all preferences that are stored in the {@link
 * SharedPreferences}. Each setting has a get/set function and a {@code public static final String}
 * declaration with the key, prefixed with PREF_. For example:
 * <p/>
 * <pre>
 *   // Boolean value. Whether the username should be remembered the next time the user visits the LoginActivity.
 *   public static final String PREF_REMEMBER_USERNAME = "pref_remember_username";
 * </pre>
 * <p/>
 * You can use some convenience methods to prevent a lot of boiler plate code.
 *
 * @see #putString(String, String)
 */
public class PreferenceManager {

    SharedPreferences sharedPreferences;

    static PreferenceManager instance;

    private PreferenceManager(Context context) {
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Clears all current sharedPreferences and replaces them with our default ones.
     *
     * @param context the context of the application that uses the method
     */
    public static void applyDefaultPreferences(Context context) {
        L.d("*");
        PreferenceManager preferenceManager = get(context);

        String lastUsedUsername = preferenceManager
                .isRememberUsernameEnabled() ?
                    preferenceManager.getLastUsedUsername() :
                    null;


        clearAllSettings(context);

        preferenceManager.setLastUsedUsername(lastUsedUsername);
        preferenceManager.setRememberUsernameEnabled(lastUsedUsername != null);
        preferenceManager.setNotificationRingtone(null);
        preferenceManager.setNotificationsEnabled(true);
        preferenceManager.setNotificationVibrationEnabled(true);
        preferenceManager.setShowPlayServicesDialog(true);
    }

    /**
     * Clears all the shared preferences
     *
     * @param context
     */
    public static void clearAllSettings(Context context) {
        PreferenceManager prefs = get(context);
        prefs.sharedPreferences.edit().clear().commit();
    }

    /**
     * @param context
     *         Context to access {@link SharedPreferences}
     *
     * @return Returns an instance of this PreferenceManager
     */
    public static PreferenceManager get(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    /**
     * @param fragment
     *         Context to access {@link SharedPreferences}
     *
     * @return Returns an instance of this PreferenceManager
     */
    public static PreferenceManager get(Fragment fragment) {
        if (instance == null) {
            instance = new PreferenceManager(fragment.getActivity());
        }
        return instance;
    }




    //----------------------------------------------------------------------------------------------

    /**
     * Boolean value. Whether the username should be saved when a login request succeeds. The password should never be
     * saved. Default false.
     *
     * @see #isRememberUsernameEnabled()
     */
    public static final String PREF_REMEMBER_USERNAME_ENABLED = "pref_remember_username_enabled";

    /**
     * Returns whether the username should be saved when a login request succeeds.
     *
     * @return whether the username should be saved when a login request succeeds.
     */
    public boolean isRememberUsernameEnabled() {
        return sharedPreferences.getBoolean(PREF_REMEMBER_USERNAME_ENABLED, false);
    }

    /**
     * Set whether the the username should be saved when a login request succeeds.
     *
     * @param enabled
     *         whether the the username should be saved when a login request succeeds
     */
    public void setRememberUsernameEnabled(boolean enabled) {
        L.d("{0}",enabled);
        putBoolean(PREF_REMEMBER_USERNAME_ENABLED, enabled);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * String value. The username that was used with the last successful login request. {@code null} when none found. To
     * remove the saved username, use {@link #removeLastUsedUsername()}.
     *
     * @see #getLastUsedUsername()
     * @see #setLastUsedUsername(String)
     * @see #removeLastUsedUsername()
     */
    public static final String PREF_LAST_USED_USERNAME = "pref_last_used_username";

    /**
     * Returns the username that was used with the last successful login request. {@code null} when none found.
     *
     * @return The username that was used with the last successful login request. {@code null} when none found.
     *
     * @see #setLastUsedUsername(String)
     * @see #removeLastUsedUsername()
     */
    public String getLastUsedUsername() {
        return sharedPreferences.getString(PREF_LAST_USED_USERNAME, null);
    }

    /**
     * Saves the username that was used with the last successful login request. {@code null} when none found.
     *
     * @param lastUsedUsername
     *         The username that was used with the last successful login request
     *
     * @see #getLastUsedUsername()
     * @see #removeLastUsedUsername()
     */
    public void setLastUsedUsername(String lastUsedUsername) {
        L.d("{0}",lastUsedUsername);
        putString(PREF_LAST_USED_USERNAME, lastUsedUsername);
    }

    /**
     * Removes the {@link #PREF_LAST_USED_USERNAME} from the shared preferences
     *
     * @see #getLastUsedUsername()
     * @see #setLastUsedUsername(String)
     */
    public void removeLastUsedUsername() {
        remove(PREF_LAST_USED_USERNAME);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * String value. The username that was used with the last successful login request. {@code null} when none found. To
     * remove the saved username, use {@link #removeLastUsedUsername()}.
     *
     * @see #getLastPassword()
     * @see #setLastPassword(String)
     */
    public static final String PREF_LAST_PASS = "pref_last_pass";

    /**
     * Returns the username that was used with the last successful login request. {@code null} when none found.
     *
     * @return The username that was used with the last successful login request. {@code null} when none found.
     *
     * @see #setLastUsedUsername(String)
     * @see #removeLastUsedUsername()
     */
    public String getLastPassword() {
        return sharedPreferences.getString(PREF_LAST_PASS, null);
    }

    /**
     * Saves the username that was used with the last successful login request. {@code null} when none found.
     *
     * @param lastSession
     *         The username that was used with the last successful login request
     *
     * @see #getLastPassword() ()
     */
    public void setLastPassword(String lastSession) {
        L.d("{0}",lastSession);
        putString(PREF_LAST_PASS, lastSession);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Boolean value. Indicating whether the user enabled notifications in the settings screen
     */
    public static final String PREF_KEY_NOTIFICATIONS_ENABLED = "pref_notifications_enabled";

    /**
     * @return Returns whether notifications are enabled for the whole app. This is a user setting.
     */
    public boolean isNotificationsEnabled() {
        return sharedPreferences.getBoolean(PREF_KEY_NOTIFICATIONS_ENABLED, true);
    }

    /**
     * @param notificationsEnabled Set whether notifications should be enabled in the whole app. This is a user setting.
     */
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        L.d("{0}",notificationsEnabled);
        putBoolean(PREF_KEY_NOTIFICATIONS_ENABLED, notificationsEnabled);
    }

    /**
     * Ringtone preference. The notification sound.
     */
    public static final String PREF_KEY_NOTIFICATION_SOUND = "pref_notification_sound";

    /**
     * @return The URI of the sound that should be used with a notification. Use with {@link android.net.Uri#parse(String)}. This is
     * a user setting.
     */
    public String getNotificationRingtone() {
        return sharedPreferences.getString(PREF_KEY_NOTIFICATION_SOUND, "");
    }

    /**
     * @param notificationRingtone The URI of the sound that should be used with a notification. This is a user setting.
     */
    public void setNotificationRingtone(String notificationRingtone) {
        L.d("{0}",notificationRingtone);
        putString(PREF_KEY_NOTIFICATION_SOUND, notificationRingtone);
    }

    /**
     * Boolean value. Whether vibration is enabled with notifications
     */
    public static final String PREF_KEY_NOTIFICATION_VIBRATE = "pref_notification_vibrate";

    /**
     * @return Returns whether the device should vibrate when a notification is fired. This is a user setting.
     */
    public boolean isNotificationVibrationEnabled() {
        return sharedPreferences.getBoolean(PREF_KEY_NOTIFICATION_VIBRATE, true);
    }

    /**
     * @param notificationVibrationEnabled Set whether the device should vibrate when a notification is fired. This is a user setting.
     */
    public void setNotificationVibrationEnabled(boolean notificationVibrationEnabled) {
        L.d("{0}",notificationVibrationEnabled);
        putBoolean(PREF_KEY_NOTIFICATION_VIBRATE, notificationVibrationEnabled);
    }

    /**
     * Boolean value. Indicates whether an informational dialog about Google Play Services should be shown to the user.
     * This preference should be set to false when the user dismisses the dialog.
     */
    public static final String PREF_KEY_SHOW_PLAY_SERVICES_DIALOG = "pref_key_show_play_services_dialog";

    /**
     * Returns whether an informational dialog about Google Play Services should be shown to the user. This preference
     * should be set to false when the user dismisses the dialog.
     *
     * @return True if the dialog should be shown, false otherwise
     */
    public boolean getShowPlayServicesDialog() {
        return sharedPreferences.getBoolean(PREF_KEY_SHOW_PLAY_SERVICES_DIALOG, true);
    }

    /**
     * Sets whether an informational dialog about Google Play Services should be shown to the user. This preference
     * should be set to false when the user dismisses the dialog.
     *
     * @param shouldBeShownNextTime Whether the dialog should be shown next time {@link #getShowPlayServicesDialog()}
     *                              is called
     */
    public void setShowPlayServicesDialog(boolean shouldBeShownNextTime) {
        L.d("{0}",shouldBeShownNextTime);
        putBoolean(PREF_KEY_SHOW_PLAY_SERVICES_DIALOG, shouldBeShownNextTime);
    }







    /* Convenience methods */

    /**
     * Convenience method that stores a String with the supplied key in {@link #sharedPreferences} object.
     * {@link SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * Convenience method that stores a Integer with the supplied key in {@link #sharedPreferences} object.
     * {@link SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * Convenience method that stores a Long with the supplied key in {@link #sharedPreferences} object.
     * {@link SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    /**
     * Convenience method that stores a Float with the supplied key in {@link #sharedPreferences} object.
     * {@link SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    /**
     * Convenience method that stores a Boolean with the supplied key in {@link #sharedPreferences} object.
     * {@link SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Convenience method that removes a preference from the {@link #sharedPreferences} object.
     * {@link SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to remove.
     */
    private void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }


}
