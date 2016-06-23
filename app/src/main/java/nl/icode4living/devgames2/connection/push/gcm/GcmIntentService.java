package nl.icode4living.devgames2.connection.push.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import nl.icode4living.devgames2.DevGamesApplication;
import nl.icode4living.devgames2.R;
import nl.icode4living.devgames2.connection.task.LogoutTask;
import nl.icode4living.devgames2.ui.activity.LoginActivity;
import nl.icode4living.devgames2.ui.activity.MainActivity;
import nl.icode4living.devgames2.util.L;
import nl.icode4living.devgames2.util.PreferenceManager;


public class GcmIntentService extends IntentService{

    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public GcmIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();

            if (extras == null || extras.isEmpty()) {
                L.v("Empty push message received");
                return;
            }

            GcmMessageType type = GcmMessageType.valueOf(intent.getStringExtra("type"));

            String notificationText;

            switch (type) {

                case PLAIN_NOTIFICATION:

                        notificationText = intent.getStringExtra("text");
                        if ((notificationText == null || notificationText.isEmpty())) {
                            notificationText = getString(R.string.new_message);
                        }

                        showNotification(
                                this,
                                GcmMessageType.PLAIN_NOTIFICATION.ordinal(),
                                getString(R.string.app_name),
                                notificationText,
                                notificationText,
                                false
                        );

                    break;


                case REGISTERED_ELSEWHERE:

                    notificationText = getString(R.string.new_device_registered);

                    showNotification(
                            this,
                            GcmMessageType.REGISTERED_ELSEWHERE.ordinal(),
                            getString(R.string.app_name),
                            notificationText,
                            notificationText,
                            true
                    );

                    new LogoutTask(this, false).executeThreaded();
                    PreferenceManager.applyDefaultPreferences(this);
                    DevGamesApplication.get(this).setLoggedInUser(null);

                    break;

                case NEW_PUSH_RECEIVED:

                    notificationText = getString(R.string.new_score, intent.getStringExtra("text"));
//                    if ((notificationText == null || notificationText.isEmpty())) {
//                        notificationText = getString(R.string.new_score_default);
//                    }

                    showNotification(
                            this,
                            GcmMessageType.NEW_PUSH_RECEIVED.ordinal(),
                            getString(R.string.app_name),
                            notificationText,
                            notificationText,
                            true
                    );


                    break;

                default:
                    L.w("Type is not a known type in Message.Type: " + String.valueOf(type));
            }


        }
        finally {
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    protected void showNotification(Context context, final int id, String title, String content, String ticker,
                                    boolean forceNotification) {

        PreferenceManager preferenceManager = PreferenceManager.get(context);

        // Skip isNotificationsEnabled() check when forcing a notification
        if (!forceNotification) {

            // Exit this function when the user does not like to get notifications
            if (!preferenceManager.isNotificationsEnabled()) {
                L.d("Showing notification skipped, disabled by user setting");
                return;
            }
        }
        else {
            L.w("showNotification: Forcing a notification, while the user preferred to have no notifications!");
        }

        // If the given title is null, use the app name
        if (title == null) {
            title = context.getString(R.string.app_name);
        }

        if (content == null) {
            content = "";
        }

        if (ticker == null) {
            ticker = "";
        }

        final NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;

        // Build the intent that will be fired when the user clicks the notification
        Intent notificationIntent =
                id != GcmMessageType.REGISTERED_ELSEWHERE.ordinal() ?
                        new Intent(context, MainActivity.class) :
                        new Intent(context, LoginActivity.class);

        // We don't like to have the same activity alive twice
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationIntent.putExtra("random", System.currentTimeMillis());

        // The pending intent enables android to fire notification intent when the user clicks the notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true) // Removes the notification from the notification centre when clicked
                .setContentIntent(contentIntent)
                .setTicker(ticker) // The text that scrolls through the notification bar when the notification is added
                .setContentTitle(title) // The title of the notification
                .setContentText(content) // The second line of the notification
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.devgames_logo_icon))
                .setSmallIcon(R.drawable.devgames_logo_icon_sword)
                .setWhen(System.currentTimeMillis()) // The time when the event for this notification happened
                .build();

        // Retrieve the URI of the ringtone that the user set for notifications
        String customSoundUri = preferenceManager.getNotificationRingtone();

        // An empty uri means no ringtone. This is displayed to the user in settings as 'silent'
        if (customSoundUri.length() > 0) {

            // Parse the uri
            notification.sound = Uri.parse(customSoundUri);

            // This is a notification. Other audio streams will be semi muted if the given ringtone is played
            notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
        }

        // Retrieve whether the device has to vibrate when adding the notification
        boolean vibrate = PreferenceManager.get(context).isNotificationVibrationEnabled();
        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        // Fire the notification on the UI thread
        final Notification finalNotification = notification;
        Runnable runnable = new Runnable() {

            public void run() {
                nm.notify(id, finalNotification);
            }
        };

        uiThreadHandler.post(runnable);
    }

}
