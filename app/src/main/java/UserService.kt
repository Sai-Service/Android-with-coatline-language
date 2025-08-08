import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("/SaiVehicleDelivery/login/vehDelvLogin3/")  // for production and clone this remains same
//    @POST("/login/vehDelvLogin3/") // for localhost

    fun userLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
