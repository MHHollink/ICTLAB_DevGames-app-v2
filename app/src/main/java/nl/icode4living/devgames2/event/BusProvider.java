package nl.icode4living.devgames2.event;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import nl.icode4living.devgames2.util.L;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus() {

        private final Handler uiHandler = new Handler(Looper.getMainLooper());

        @Override
        public void register(Object subscriber) {
            L.d("register: {0}", subscriber);
            super.register(subscriber);
        }

        @Override
        public void unregister(Object subscriber) {
            L.d("unregister: {0}", subscriber);
            super.unregister(subscriber);
        }

        @Override
        public void post(final Object event) {

            L.d("post: {0}", event);

            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        post(event);
                    }
                });
            }
        }
    };

    public static Bus getBus() {
        return BUS;
    }

    private BusProvider() {
    }
}
