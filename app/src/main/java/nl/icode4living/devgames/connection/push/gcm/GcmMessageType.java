package nl.icode4living.devgames.connection.push.gcm;

public enum GcmMessageType {
    PLAIN_NOTIFICATION,     // Just show the 'text' in the notification
    REGISTERED_ELSEWHERE,   // Show R.Strings.new_device_registered AND remove all saved data
    NEW_PUSH_RECEIVED,      // Show R.Strings.new_score             AND request update for this push.
    ACCOUNT_UPDATED,        // No notification                      AND Call DevGamesClient.getCurrentlyLoggedInUser()
    BROKEN_BUILD            // Show R.Strings.___
}
