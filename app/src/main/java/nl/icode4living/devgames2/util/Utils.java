package nl.icode4living.devgames2.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

/**
 * Some static utility methods.
 */
@SuppressWarnings("unused")
public final class Utils {

    private static Boolean isInDevelopment = null;

    // Private constructor: no need to instantiate this class.
    private Utils() {
    }

    /**
     * Converts a given Bitmap into a JPEG image's base 64 string.
     *
     * @param image
     *         the image to convert.
     *
     * @return a JPEG base 64 string of a given bitmap.
     */
    public static String encodeBase64(Bitmap image) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] array = out.toByteArray();
        return Base64.encodeToString(array, Base64.DEFAULT);
    }

    /**
     * Decodes a base 64 string into a Bitmap.
     *
     * @param input
     *         the image data, in base 64.
     *
     * @return a base 64 string into a Bitmap.
     */
    public static Bitmap decodeBase64(String input) {

        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /**
     * Returns true if `date` is on the current day.
     *
     * @param date
     *         the Date to check.
     *
     * @return true if `date` is on the current day.
     */
    public static boolean isToday(Date date) {

        Calendar today = Calendar.getInstance();

        Calendar other = Calendar.getInstance();
        other.setTime(date);

        return (today.get(Calendar.YEAR) == other.get(Calendar.YEAR)) &&
                (today.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * Returns true if `date` is on the current day.
     *
     * @param calendar
     *         the Date to check.
     *
     * @return true if `date` is on the current day.
     */
    public static boolean isToday(Calendar calendar) {

        Calendar today = Calendar.getInstance();

        return (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) &&
                (today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * Returns a preview of this message. The message is split on
     * whitespaces after which N words are returned whose total
     * number of characters won't exceed `maxChars`. The preview
     * is then returned with "..." appended to it.
     * <p/>
     * The maximum length of the preview *can* be longer than
     * `message` however:
     * <p/>
     * <pre>
     * getPreview("abc d", 4) // returns: "abc ..."
     * </pre>
     *
     * @param message
     *         the message to create a preview from.
     * @param maxChars
     *         the max number of characters from the message to include
     *         in the preview.
     *
     * @return a preview of this message, or an empty string when `message`
     * is `null` or `maxChars` is less than zero.
     */
    public static String getPreview(String message, int maxChars) {

        // Return an empty string upon invalid parameters.
        if (message == null || maxChars < 1) {
            return "";
        }

        // Check if there's a need to create a preview.
        if (message.length() <= maxChars) {
            return message;
        }

        StringBuilder builder = new StringBuilder();

        // Iterate over all words.
        for (String word : message.split("\\s++")) {

            // Check if we need to stop.
            if ((builder.length() + word.length()) > maxChars) {
                break;
            }

            builder.append(word).append(" ");
        }

        return builder.append("...").toString();
    }

    /**
     * Checks if the currently running app is signed with the development key.
     * If {@code context} is null, true is also returned.
     *
     * @param context
     *         the context from which the method is called.
     *
     * @return true iff the currently running app is signed with the
     * development key. If <code>context</code> is null, true is
     * also returned.
     */
    public static boolean isInDevelopment(Context context) {

        if (isInDevelopment != null) {
            return isInDevelopment;
        }

        if (context == null) {
            L.d("# context == null, in development");
            isInDevelopment = true;
            return true;
        }

        try {
            PackageInfo info = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            Signature[] signatures = info.signatures;

            if (signatures == null) {
                // When running unit tests, there will be no signatures.
                L.d("# signatures == null, in development");
                isInDevelopment = true;
                return true;
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (Signature signature : signatures) {

                ByteArrayInputStream stream = new ByteArrayInputStream(signature.toByteArray());
                X509Certificate certificate = (X509Certificate) cf.generateCertificate(stream);

                if (certificate.getSubjectX500Principal().equals(new X500Principal("CN=Android Debug,O=Android,C=US"))) {
                    L.d("# found development certificate, in development");
                    isInDevelopment = true;
                    return true;
                }
            }
        }
        catch (Exception e) {
            // Ignore this.
        }

        L.d("# not in development!");

        return false;
    }

    /**
     * A null-safe wrapper around {@link Arrays#deepToString(Object[])}
     *
     * @param collection
     *         The collection you'd like to stringify
     *
     * @return 'null' when {@code collection == null}, otherwise the result from {@link Arrays#deepToString(Object[])}
     */
    public static String collectionToString(Collection collection) {
        return collection != null ? Arrays.deepToString(collection.toArray()) : "null";
    }

    public static <T> T[] toArray(Collection<T> collection, Class<T> clazz) {
        if (collection == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(clazz, collection.size());
        return collection.toArray(arr);
    }

    /**
     * Returns true when there's a working internet connection available, otherwise false. Uses the
     * {@link ConnectivityManager}
     *
     * @param context
     *         Context to determine whether a working connection is available
     *
     * @return Returns true when there's a working internet connection available, otherwise false
     */
    public static boolean hasInternetConnection(Context context) {

        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isEmpty(Collection collection) {
        return collection != null && collection.size() == 0;
    }

    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }

    public static boolean isEmpty(Map map) {
        return map != null && map.size() == 0;
    }

    public static boolean isNotEmpty(Map map) {
        return map != null && map.size() > 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array != null && array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNotEmpty(String string) {
        return string != null && string.length() != 0;
    }

    public static boolean isNotEmptyAndEquals(String a, String b) {
        return a != null && b != null && a.length() != 0 && b.length() != 0 && a.equals(b);
    }

    public static int count(Collection collection) {
        return collection != null ? collection.size() : 0;
    }

    public static int count(Map map) {
        return map != null ? map.size() : 0;
    }

    public static int count(Object[] array) {
        return array != null ? array.length : 0;
    }

    /**
     * Copies text to the clipboard. If {@link Build.VERSION#SDK_INT} {@code &lt;} {@link Build.VERSION_CODES#HONEYCOMB},
     * the {@link android.text.ClipboardManager} is used, otherwise {@link android.content.ClipboardManager}.
     *
     * @param context
     *         Context to access the ClipBoardManager
     * @param text
     *         The text to be copied
     */
    public static void copyTextToClipboard(Context context, final String text) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        }
        else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog (using {@link com.google.android.gms.common.GooglePlayServicesUtil#getErrorDialog(int, android.app.Activity, int)}) that allows
     * users to download the APK from the Google Play Store or enable it in the device's system settings.
     */
    public static boolean playServicesAvailable(Context context) {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    /**
     * Returns the version code from the manifest
     *
     * @return The version code from the manifest
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            L.e("Could not retrieve versionName");
            return -1;
        }
    }

    /**
     * Returns the version name from the manifest.
     *
     * @return The version name from the manifest
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            L.e("Could not retrieve versionName");
            return "";
        }
    }

    public static String valueOrDefault(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Copied from http://android-developers.blogspot.nl/2009/01/can-i-use-this-intent.html
     *
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {

        if (context == null) {
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();

        if (packageManager == null) {
            return false;
        }

        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() != 0;
    }

    public static Uri getDefaultRingtone() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    /**
     * Create and shows a toast on the given fragment/activity with the given String for the given Duration
     *
     * @param context the contact of the activity that will show the Toast
     * @param message the message inside the toast
     * @param duration the duration from Toast. Either {@link Toast#LENGTH_SHORT} OR {@link Toast#LENGTH_LONG}
     */
    public static void createToast(Context context, String message, int duration){
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.TOP, 0, 30);
        toast.show();
    }


    /**
     * see {@link #createToast(Context, String, int)}
     */
    public static void createToast(Context context, int resId, int duration){
        createToast(context, context.getString(resId), duration);
    }


    /**
     * see {@link #createToast(Context, String, int)}
     */
    public static void createToast(Fragment fragment, String message, int duration){
        createToast(fragment.getContext(), message, duration);
    }

    /**
     * see {@link #createToast(Context, String, int)}
     */
    public static void createToast(Fragment fragment, int resId, int duration){
        createToast(fragment.getContext(), resId, duration);
    }
}