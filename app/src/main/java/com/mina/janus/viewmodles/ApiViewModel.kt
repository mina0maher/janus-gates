package com.mina.janus.viewmodles


import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.mina.janus.R
import com.mina.janus.apis.RetrofitFactory
import com.mina.janus.models.AddressModel
import com.mina.janus.models.DirectionsModel
import com.mina.janus.models.UserLoginModel
import com.mina.janus.models.UserRegisterModel
import com.mina.janus.utilities.Constants
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class ApiViewModel : ViewModel() {


     var codesMD: SingleLiveEvent<Int> = SingleLiveEvent()
     val codesLiveData:LiveData<Int>
          get() = codesMD

    var registerBodyMD: SingleLiveEvent<UserRegisterModel> = SingleLiveEvent()
    val bodyLiveData:LiveData<UserRegisterModel>
        get() = registerBodyMD

    var errorMessageMD: SingleLiveEvent<String> = SingleLiveEvent()
    val errorMessageLiveData:LiveData<String>
        get() = errorMessageMD

    var directionsBodyMD: SingleLiveEvent<DirectionsModel> = SingleLiveEvent()
    val directionsBodyLiveData:LiveData<DirectionsModel>
        get() = directionsBodyMD


    fun signIn(userLoginModel: UserLoginModel){
         RetrofitFactory.apiInterface().logIn(userLoginModel)
             .enqueue(object :retrofit2.Callback<UserRegisterModel>{
            override fun onResponse(
                call: Call<UserRegisterModel>,
                response: Response<UserRegisterModel>
            ) {
                codesMD.postValue(response.code())

                if(response.code()==200) {
                    registerBodyMD.postValue(response.body())
                }
            }
            override fun onFailure(call: Call<UserRegisterModel>, t: Throwable) {
                errorMessageMD.postValue(t.message.toString())
            }

        })
    }

    fun signUp(userRegisterModel: UserRegisterModel){
        RetrofitFactory.apiInterface().register(userRegisterModel)
            .enqueue(object :retrofit2.Callback<UserRegisterModel>{
                override fun onResponse(
                    call: Call<UserRegisterModel>,
                    response: Response<UserRegisterModel>
                ) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        registerBodyMD.postValue(response.body())
                    }
                }
                override fun onFailure(call: Call<UserRegisterModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }
    fun getDirections(addressModel: AddressModel){
        RetrofitFactory.apiInterface().getDirections(addressModel)
            .enqueue(object :retrofit2.Callback<DirectionsModel>{
                override fun onResponse(call: Call<DirectionsModel>, response: Response<DirectionsModel>) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        directionsBodyMD.postValue(response.body())
                    }

                }

                override fun onFailure(call: Call<DirectionsModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }
}

