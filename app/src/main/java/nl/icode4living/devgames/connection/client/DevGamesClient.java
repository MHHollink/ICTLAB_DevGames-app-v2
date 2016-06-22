package nl.icode4living.devgames.connection.client;

import java.util.List;
import java.util.Map;

import nl.icode4living.devgames.models.Project;
import nl.icode4living.devgames.models.User;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
public interface DevGamesClient {


    @FormUrlEncoded
    @POST("/login")
    Map<String, String> login(@Field("username") String user, @Field("password") String pass);

    @GET("/users")
    User getCurrentlyLoggedInUser();

    @PUT("/users/{id}")
    Response updateUser(@Body User user, @Path("id") Long id);

    @GET("/users/{id}")
    User getUserById(@Path("id") Long id);

    @GET("/users/{id}/projects")
    List<Project> getProjectsOfUser(@Path("id") Long id);

    @GET("/projects/{id}")
    Project getProjectById(@Path("id") Long id);

    @GET("/projects/{id}/users")
    List<User> getUsersFromProject(@Path("id") Long id);

    @GET("/projects/{id}/totalScoresOfUsers")
    List<User> getHighScoresForProject(@Path("id") Long id);


}
