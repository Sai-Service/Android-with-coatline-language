import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
//    @POST("/ErpAndroid/login/loginpage/") // for production and clone this remains same
    @POST("/login/loginpage/") // for localhost
    fun userLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
