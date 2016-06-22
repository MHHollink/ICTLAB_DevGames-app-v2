package nl.icode4living.devgames.connection.push.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import nl.icode4living.devgames.util.L;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        L.v("Received!");

        // Get instance of Google Cloud Messaging
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        // Get the extras for the intent
        Bundle extras = intent.getExtras();

        // Check if the intent does actually have an extra
        if(extras == null || extras.isEmpty())
        {
            L.w("Empty push message received... Service not started. extras={0}",extras);
            return;
        }

        /**
         * Check if the type of the message is equal to {@Link com.google.android.gms.gcm.GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE}
         */
        String type = gcm.getMessageType(intent);
        if(!GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(type))
        {
            L.w("Unknown message type from GCM! service not started. type="+type);
            return;
        }


        ComponentName componentName = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, intent.setComponent(componentName));
    }
}
