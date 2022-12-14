package com.mina.janus.viewmodles


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mina.janus.apis.RetrofitFactory
import com.mina.janus.models.*
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

    var gatesBodyMD: SingleLiveEvent<GatesModel> = SingleLiveEvent()
    val gatesBodyLiveData:LiveData<GatesModel>
        get() = gatesBodyMD

    var carBodyMD: SingleLiveEvent<CarModel> = SingleLiveEvent()
    val carBodyLiveData:LiveData<CarModel>
        get() = carBodyMD

    var jsessionidMD: SingleLiveEvent<String> = SingleLiveEvent()
    val jsessionidLiveData:LiveData<String>
        get() = jsessionidMD

    fun signIn(userLoginModel: UserLoginModel){
         RetrofitFactory.apiInterface().logIn(userLoginModel)
             .enqueue(object :retrofit2.Callback<UserRegisterModel>{
            override fun onResponse(
                call: Call<UserRegisterModel>,
                response: Response<UserRegisterModel>
            ) {
                val cookieList = response.headers().values("Set-Cookie")
                val jsessionid = cookieList[0].split(";").toTypedArray()[0]
                jsessionidMD.postValue(jsessionid)

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
                    val cookieList = response.headers().values("Set-Cookie")
                    val jsessionid = cookieList[0].split(";").toTypedArray()[0]
                    jsessionidMD.postValue(jsessionid)
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
    fun getAllGates(){
        RetrofitFactory.apiInterface().getGates()
            .enqueue(object :retrofit2.Callback<GatesModel>{
                override fun onResponse(call: Call<GatesModel>, response: Response<GatesModel>) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        gatesBodyMD.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<GatesModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }
    fun getCars(sessionId:String){
        RetrofitFactory.apiInterface().getCars(sessionId)
            .enqueue(object :retrofit2.Callback<CarModel>{
                override fun onResponse(call: Call<CarModel>, response: Response<CarModel>) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        carBodyMD.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<CarModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }
}

