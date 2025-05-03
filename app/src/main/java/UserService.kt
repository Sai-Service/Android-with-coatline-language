import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
 // for production and clone this remains same
      @POST("/AndroidFA/EmpMst/loginpage/")

//    @POST("/EmpMst/loginpage/") // for localhost

    fun userLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
