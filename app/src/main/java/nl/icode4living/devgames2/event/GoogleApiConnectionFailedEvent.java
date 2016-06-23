package nl.icode4living.devgames2.event;

import com.google.android.gms.common.ConnectionResult;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 23-6-2016.
 */
public class GoogleApiConnectionFailedEvent {
    public ConnectionResult connectionResult;

    public GoogleApiConnectionFailedEvent(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }
}
