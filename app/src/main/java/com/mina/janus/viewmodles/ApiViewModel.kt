package com.mina.janus.viewmodles


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mina.janus.apis.RetrofitFactory
import com.mina.janus.models.*
import retrofit2.Call
import retrofit2.Response

class ApiViewModel : ViewModel() {


     private var codesMD: SingleLiveEvent<Int> = SingleLiveEvent()
     val codesLiveData:LiveData<Int>
          get() = codesMD

    private var registerBodyMD: SingleLiveEvent<UserRegisterModel> = SingleLiveEvent()
    val bodyLiveData:LiveData<UserRegisterModel>
        get() = registerBodyMD

    private var errorMessageMD: SingleLiveEvent<String> = SingleLiveEvent()
    val errorMessageLiveData:LiveData<String>
        get() = errorMessageMD

    private var directionsBodyMD: SingleLiveEvent<DirectionsModel> = SingleLiveEvent()
    val directionsBodyLiveData:LiveData<DirectionsModel>
        get() = directionsBodyMD

    private var routesBodyMD: SingleLiveEvent<RoutesModel> = SingleLiveEvent()
    val routesBodyLiveData:LiveData<RoutesModel>
        get() = routesBodyMD

    private var gatesBodyMD: SingleLiveEvent<GatesModel> = SingleLiveEvent()
    val gatesBodyLiveData:LiveData<GatesModel>
        get() = gatesBodyMD

    private var carsBodyMD: SingleLiveEvent<CarModel> = SingleLiveEvent()
    val carsBodyLiveData:LiveData<CarModel>
        get() = carsBodyMD

    private var jsessionidMD: SingleLiveEvent<String> = SingleLiveEvent()
    val jsessionidLiveData:LiveData<String>
        get() = jsessionidMD

    private var ticketBodyMD: SingleLiveEvent<TicketsResponseModel> = SingleLiveEvent()
    val ticketBodyLiveData:LiveData<TicketsResponseModel>
        get() = ticketBodyMD

    private var statusBodyMD: SingleLiveEvent<StatusModel> = SingleLiveEvent()
    val statusBodyLiveData:LiveData<StatusModel>
        get() = statusBodyMD

    private var carBodyMD: SingleLiveEvent<CarModelItem> = SingleLiveEvent()
    val carBodyLiveData:LiveData<CarModelItem>
        get() = carBodyMD

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
                        carsBodyMD.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<CarModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }
    fun reserveTicket(sessionId:String,ticketPostModel: TicketPostModel){
        RetrofitFactory.apiInterface().reserveTicket(sessionId,ticketPostModel)
            .enqueue(object :retrofit2.Callback<TicketsResponseModel>{
                override fun onResponse(
                    call: Call<TicketsResponseModel>,
                    response: Response<TicketsResponseModel>
                ) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        ticketBodyMD.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<TicketsResponseModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }

    fun getRoute(addressModel: AddressModel){
        RetrofitFactory.apiInterface().getRoute(addressModel)
            .enqueue(object :retrofit2.Callback<RoutesModel>{
                override fun onResponse(call: Call<RoutesModel>, response: Response<RoutesModel>) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        routesBodyMD.postValue(response.body())
                    }

                }

                override fun onFailure(call: Call<RoutesModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }

    fun getStatus(sessionId:String){
        RetrofitFactory.apiInterface().getStatus(sessionId)
            .enqueue(object :retrofit2.Callback<StatusModel>{
                override fun onResponse(call: Call<StatusModel>, response: Response<StatusModel>) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        statusBodyMD.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<StatusModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }

    fun addCar(sessionId:String,carPostModel: CarPostModel){
        RetrofitFactory.apiInterface().addCar(sessionId,carPostModel)
            .enqueue(object :retrofit2.Callback<CarModelItem>{
                override fun onResponse(
                    call: Call<CarModelItem>,
                    response: Response<CarModelItem>
                ) {
                    codesMD.postValue(response.code())
                    if(response.code()==200){
                        carBodyMD.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<CarModelItem>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }
}

