package com.mina.janus.viewmodles


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mina.janus.apis.RetrofitFactory
import com.mina.janus.models.UserLoginModel
import com.mina.janus.models.UserRegisterModel
import retrofit2.Call
import retrofit2.Response

class ApiViewModel : ViewModel() {


     var codesMD: SingleLiveEvent<Int> = SingleLiveEvent()
     val codesLiveData:LiveData<Int>
          get() = codesMD

    var bodyMD: SingleLiveEvent<UserRegisterModel> = SingleLiveEvent()
    val bodyLiveData:LiveData<UserRegisterModel>
        get() = bodyMD

    var errorMessageMD: SingleLiveEvent<String> = SingleLiveEvent()
    val errorMessageLiveData:LiveData<String>
        get() = errorMessageMD


    fun signIn(userLoginModel: UserLoginModel){
         RetrofitFactory.apiInterface().logIn(userLoginModel)
             .enqueue(object :retrofit2.Callback<UserRegisterModel>{
            override fun onResponse(
                call: Call<UserRegisterModel>,
                response: Response<UserRegisterModel>
            ) {
                codesMD.postValue(response.code())

                if(response.code()==200) {
                    bodyMD.postValue(response.body())
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
                        bodyMD.postValue(response.body())
                    }
                }
                override fun onFailure(call: Call<UserRegisterModel>, t: Throwable) {
                    errorMessageMD.postValue(t.message.toString())
                }

            })
    }
}

