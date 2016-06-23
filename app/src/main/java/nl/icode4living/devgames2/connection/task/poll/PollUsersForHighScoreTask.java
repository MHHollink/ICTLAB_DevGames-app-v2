package nl.icode4living.devgames2.connection.task.poll;

import android.content.Context;

import java.util.List;

import nl.icode4living.devgames2.connection.task.RESTTask;
import nl.icode4living.devgames2.event.BusProvider;
import nl.icode4living.devgames2.event.PollUsersTaskFinishedEvent;
import nl.icode4living.devgames2.models.User;
import nl.icode4living.devgames2.util.L;
import nl.icode4living.devgames2.util.Utils;
import retrofit.RetrofitError;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public class PollUsersForHighScoreTask extends RESTTask<Void, Void, Integer> {

    private long projectId;

    /**
     * Creates a new instance of this REST task.
     *
     * @param context the context from which this task was created.
     */
    public PollUsersForHighScoreTask(Context context, Long projectId) {
        super(context);
        this.projectId = projectId;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (!Utils.hasInternetConnection(getApplication())) return null;

        List<User> users;
        try {
            users = createService().getHighScoresForProject(projectId);
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

        L.v("Retrieved users {0}", users);

        if (users == null) {
            // Apparently, User does not exist, or we do not have sufficient rights to see the user
            return RESTTask.FORBIDDEN;
        }

        BusProvider.getBus().post(new PollUsersTaskFinishedEvent(users));
        return OK;
    }
}
