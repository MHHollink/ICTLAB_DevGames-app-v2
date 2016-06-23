package nl.icode4living.devgames2.connection.task.poll;

import android.content.Context;

import nl.icode4living.devgames2.connection.task.RESTTask;
import nl.icode4living.devgames2.event.BusProvider;
import nl.icode4living.devgames2.event.PollUserFinishedEvent;
import nl.icode4living.devgames2.models.User;
import nl.icode4living.devgames2.util.L;
import nl.icode4living.devgames2.util.Utils;
import retrofit.RetrofitError;

public class PollUserTask extends RESTTask<Void, Void, Integer> {

    private Long userId;

    public PollUserTask(Context context, Long userId) {
        super(context);
        this.userId = userId;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (!Utils.hasInternetConnection(getApplication())) return null;

        User user;
        try {
            user = createService().getUserById(userId);
        } catch (RetrofitError error) {
            int status = super.getStatus(error);

            // Also check if the user is still logged in
            // Otherwise this check will login again, that is not a good idea :)
            if ((status == FORBIDDEN || status == UNAUTHORIZED) && getLoggedInUser() != null) {
                super.requestReLogin();
                L.d("user was requested to re-login: {0}", getLoggedInUser().getId());
            }

            L.e(error, "HTTP error: {0}", status);
            return status;
        } catch (Exception e) {
            L.e(e, "something unexpected happened");
            return RESTTask.GENERAL_CONNECTION_ERROR;
        }

        L.v("Retrieved user {0}", user);

        if (user == null) {
            // Apparently, User does not exist, or we do not have sufficient rights to see the user
            return RESTTask.FORBIDDEN;
        }

        BusProvider.getBus().post(new PollUserFinishedEvent(true, user));

        return OK;
    }
}
