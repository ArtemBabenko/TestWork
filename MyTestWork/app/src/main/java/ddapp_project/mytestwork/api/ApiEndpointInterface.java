package ddapp_project.mytestwork.api;

import java.util.List;

import ddapp_project.mytestwork.object.Student;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiEndpointInterface {

    @GET("api/test/students")
    Call<List<Student>> getStudents();
}

